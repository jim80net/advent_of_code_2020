defmodule D12 do
  use Application
  alias D12.Navi
  alias D12.Navi2

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

  @impl true
  def start(_type, _args) do
    D12.Supervisor.start_link(name: D12.Supervisor)
  end

  defp info(msg) do
    IO.inspect(:stderr, msg, label: "info")
    msg
  end

  defp info(msg, label) do
    IO.inspect(:stderr, msg, label: "info " <> label)
    msg
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

  @doc false
  def parse_action_value(data_s) do
    [
      String.at(data_s, 0),
      data_s |> String.slice(1..-1) |> String.to_integer()
    ]
  end

  @doc "Manhattan distance a vec"
  def manhattan([a, b]) do
    abs(a) + abs(b)
  end

  defp read(:purge) do
    IO.read(:stdio, :all)
  end

  defp read() do
    case IO.read(:stdio, :line) do
      :eof ->
        :ok

      {:error, reason} ->
        error("Error: #{reason}")

      data ->
        [action, value] = data |> String.trim() |> parse_action_value()
        info([action, value], "Read Action Value")
        Navi.action(Navi, action, value)
        Navi2.action(Navi2, action, value)
        read()
    end
  end

  defp response({_opts, []}) do
    read()

    [
      Navi.position(Navi) ~> info("Part 1 Boat position:") ~> manhattan(),
      Navi2.position(Navi2) ~> info("Part 2 Boat position:") ~> manhattan()
    ]
  end

  def main(args \\ []) do
    args
    |> parse_args()
    |> response()
    |> info("[Part 1, Part 2]")
    |> IO.inspect()
  end
end
