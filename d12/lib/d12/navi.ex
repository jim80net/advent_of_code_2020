defmodule D12.Navi do
  use GenServer

  # Client
  def start_link(opts) do
    GenServer.start_link(__MODULE__, [0, 0, 90], opts)
  end

  @doc "Return the position of the boat"
  def position(pid) do
    GenServer.call(pid, {:position})
  end

  @doc "Return the bearing of the boat"
  def bearing(pid) do
    GenServer.call(pid, {:bearing})
  end

  @doc "Return the full state of the boat"
  def state(pid) do
    GenServer.call(pid, {:state})
  end

  @doc "Generic action to do the right thing"
  def action(pid, action, value) do
    case action do
      "N" -> move(pid, "N", value)
      "S" -> move(pid, "S", value)
      "E" -> move(pid, "E", value)
      "W" -> move(pid, "W", value)
      "L" -> rotate(pid, "L", value)
      "R" -> rotate(pid, "R", value)
      "F" -> forward(pid, value)
    end
  end

  @doc "Move the location of the boat"
  def move(pid, direction, value) do
    GenServer.cast(pid, {:move, direction, value})
  end

  @doc "Rotate the bearing of the boat"
  def rotate(pid, direction, value) do
    GenServer.cast(pid, {:rotate, direction, value})
  end

  @doc "Move the boat forward along its bearing"
  def forward(pid, value) do
    case bearing(pid) do
      90 -> move(pid, "E", value)
      180 -> move(pid, "S", value)
      270 -> move(pid, "W", value)
      0 -> move(pid, "N", value)
    end
  end

  # Server

  @impl true
  def init(state) do
    {:ok, state}
  end

  @impl true
  def handle_call({:position}, _from, [position_e, position_n, _bearing] = state) do
    {:reply, [position_e, position_n], state}
  end

  @impl true
  def handle_call({:bearing}, _from, [_position_e, _position_n, bearing] = state) do
    {:reply, bearing, state}
  end

  @impl true
  def handle_call({:state}, _from, state) do
    {:reply, state, state}
  end

  @impl true
  def handle_cast({:move, direction, value}, [position_e, position_n, bearing]) do
    [delta_e, delta_n] = direction_value(direction, value)

    {
      :noreply,
      [
        position_e + delta_e,
        position_n + delta_n,
        bearing
      ]
    }
  end

  @impl true
  def handle_cast({:rotate, direction, value}, [position_e, position_n, bearing]) do
    new_bearing = rotate_value(bearing, direction, value)

    {
      :noreply,
      [
        position_e,
        position_n,
        new_bearing
      ]
    }
  end

  @doc false
  defp direction_value(direction, value) do
    case direction do
      "N" -> [0, value]
      "S" -> [0, -value]
      "E" -> [value, 0]
      "W" -> [-value, 0]
    end
  end

  @doc false
  defp rotate_value(bearing, direction, value) do
    my_value =
      case direction do
        "R" -> value
        "L" -> -value
      end

    (bearing + my_value)
    |> rem(360)
    |> unneg()
  end

  @doc false
  defp unneg(bearing) when bearing < 0 do
    bearing + 360
  end

  @doc false
  defp unneg(bearing) do
    bearing
  end
end
