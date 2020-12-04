defmodule D4 do
  defp info(msg) do
    IO.inspect(:stderr, msg, label: "info")
    msg
  end

  defp valid?(passport) do
    required_fields = MapSet.new(~w(byr iyr eyr hgt hcl ecl pid))
    passport
    |> Map.keys()
    |> MapSet.new()
    |> MapSet.intersection(required_fields) # Limit search to only those fields that are required
    |> MapSet.equal?(required_fields) # After the intersection filter, is this equal?
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
    |> Enum.count(fn x -> x end) # Count the valids 
    |> IO.puts()
  end
end

D4.main()
