defmodule D1 do
  defp info(msg) do
    IO.puts(:stderr, msg)
  end

  defp error(msg) do
    info(msg)
    exit(1)
  end

  defp parse_args(args) do
    {opts, arguments, _} =
      args
      |> OptionParser.parse(switches: [])

    {opts, arguments}
  end

  defp read(:purge) do
    IO.read(:stdio, :all)
  end

  defp read(ledger) do
    case IO.read(:stdio, :line) do
      :eof ->
        :ok

      {:error, reason} ->
        error("Error: #{reason}")

      data ->
        value = data |> String.trim() |> String.to_integer()
        results = D1.Ledger.result(ledger, value)

        case results do
          {:ok, result} ->
            info("Found match for value: #{value} * #{2020 - value}: #{result}")
            read(:purge)
            # Early exit
            result

          {:none, nil} ->
            D1.Ledger.add(ledger, value)
            read(ledger)
        end
    end
  end

  defp response({_opts, []}) do
    {:ok, ledger} = D1.Ledger.start_link(nil)
    read(ledger)
  end

  def main(args \\ []) do
    args
    |> parse_args()
    |> response()
    |> IO.puts()
  end
end
