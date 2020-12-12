defmodule D12.Supervisor do
  use Supervisor

  def start_link(opts) do
    Supervisor.start_link(__MODULE__, :ok, opts)
  end

  @impl true
  def init(:ok) do
    children = [
      {D12.Navi, name: D12.Navi}
    ]

    Supervisor.init(children, strategy: :one_for_one)
  end
end