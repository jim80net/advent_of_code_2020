defmodule D4 do
  defmacro left ~> right do
    quote do
      case unquote(left) do
        {:error, error} ->
          {:error, error}

        result ->
          result |> unquote(right)
      end
    end
  end

  defp info(msg) do
    IO.inspect(:stderr, msg, label: "info")
    msg
  end

  defp info(msg, label) do
    IO.inspect(:stderr, msg, label: "info " <> label)
    msg
  end

  defp valid_byr?(passport) do
    valid? =
      passport
      ~> Map.fetch!("byr")
      ~> String.to_integer()
      ~> info("valid_byr?")
      ~> (fn year -> year >= 1920 and year <= 2002 end).()

    if valid? do
      passport
    else
      {:error, "Invalid BYR"}
    end
  end

  defp valid_iyr?(passport) do
    valid? =
      passport
      ~> Map.fetch!("iyr")
      ~> String.to_integer()
      ~> info("valid_iyr?")
      ~> (fn year -> year >= 2010 and year <= 2020 end).()

    if valid? do
      passport
    else
      {:error, "Invalid IYR"}
    end
  end

  defp valid_eyr?(passport) do
    valid? =
      passport
      ~> Map.fetch!("eyr")
      ~> String.to_integer()
      ~> info("valid_eyr?")
      ~> (fn year -> year >= 2020 and year <= 2030 end).()

    if valid? do
      passport
    else
      {:error, "Invalid EYR"}
    end
  end

  defp valid_hgt?(passport) do
    parsed = Map.fetch!(passport, "hgt")
    size = byte_size(parsed) - 2
    info(parsed <> "/" <> Integer.to_string(size), "valid_hgt?")

    valid? =
      case parsed do
        <<num::binary-size(size), "cm">> ->
          num
          ~> String.to_integer()
          ~> (fn cm -> cm >= 150 and cm <= 193 end).()

        <<num::binary-size(size), "in">> ->
          num
          ~> String.to_integer()
          ~> (fn inches -> inches >= 59 and inches <= 76 end).()

        _ ->
          false
      end

    if valid? do
      passport
    else
      {:error, "Invalid HGT"}
    end
  end

  defp valid_hcl?(passport) do
    parsed = Map.fetch!(passport, "hcl")
    info(parsed, "valid_hcl?")

    valid? = String.match?(parsed, ~r/#[0-9a-f]{6}$/)

    if valid? do
      passport
    else
      {:error, "Invalid HCL"}
    end
  end

  defp valid_ecl?(passport) do
    valid_ecl = ~w(amb blu brn gry grn hzl oth)
    parsed = Map.fetch!(passport, "ecl")
    info(parsed, "valid_ecl?")

    valid? = Enum.member?(valid_ecl, parsed)

    if valid? do
      passport
    else
      {:error, "Invalid ECL"}
    end
  end

  defp valid_pid?(passport) do
    parsed = Map.fetch!(passport, "pid")
    info(parsed, "valid_pid?")

    valid? = String.match?(parsed, ~r/^[0-9]{9}$/)

    if valid? do
      passport
    else
      {:error, "Invalid PID"}
    end
  end

  defp all_the_fields?(passport) do
    required_fields = MapSet.new(~w(byr iyr eyr hgt hcl ecl pid))

    valid? =
      passport
      ~> Map.keys()
      ~> MapSet.new()
      # Limit search to only those fields that are required
      ~> MapSet.intersection(required_fields)
      # After the intersection filter, is this equal?
      ~> MapSet.equal?(required_fields)

    if valid? do
      passport
    else
      {:error, "Fields missing"}
    end
  end

  defp valid?(passport) do
    result =
      passport
      ~> all_the_fields?()
      ~> valid_byr?()
      ~> valid_iyr?()
      ~> valid_eyr?()
      ~> valid_hgt?()
      ~> valid_hcl?()
      ~> valid_ecl?()
      ~> valid_pid?()

    case result do
      {:error, error} -> {:error, error}
      _ -> true
    end
  end

  defp read() do
    IO.read(:stdio, :all)
    |> String.split("\n\n")
    |> Enum.map(fn x ->
      x
      |> String.replace(~r/\n/, " ")
      |> String.split(" ")
      |> Enum.map(fn element -> String.split(element, ":") end)
      |> List.flatten()
      |> Enum.chunk_every(2)
      |> Map.new(fn [k, v] -> {k, v} end)
      |> valid?()
    end)
    |> info()
  end

  def main() do
    read()
    # Count the valids 
    |> Enum.count(fn x -> x == true end)
    |> IO.puts()
  end
end

D4.main()
