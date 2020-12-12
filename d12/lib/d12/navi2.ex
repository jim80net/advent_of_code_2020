defmodule D12.Navi2 do
  use GenServer

  # Client
  def start_link(opts) do
    GenServer.start_link(
      __MODULE__,
      [
        _position_e = 0,
        _position_n = 0,
        _waypoint_e = 10,
        _waypoint_n = 1
      ],
      opts
    )
  end

  @doc "Return the position of the boat"
  def position(pid) do
    GenServer.call(pid, {:position})
  end

  @doc "Return the position of the waypoint"
  def waypoint(pid) do
    GenServer.call(pid, {:waypoint})
  end

  @doc "Return the full state of the boat"
  def state(pid) do
    GenServer.call(pid, {:state})
  end

  @doc "Generic action to do the right thing"
  def action(pid, action, value) do
    case action do
      "N" -> move_w(pid, "N", value)
      "S" -> move_w(pid, "S", value)
      "E" -> move_w(pid, "E", value)
      "W" -> move_w(pid, "W", value)
      "L" -> rotate(pid, "L", value)
      "R" -> rotate(pid, "R", value)
      "F" -> forward(pid, value)
    end
  end

  @doc "Move the location of the boat"
  def move(pid, direction, value) do
    GenServer.cast(pid, {:move, direction, value})
  end

  @doc "Move the location of the waypoint"
  def move_w(pid, direction, value) do
    GenServer.cast(pid, {:move_w, direction, value})
  end

  @doc "Rotate the waypoint around the boat"
  def rotate(pid, direction, value) do
    GenServer.cast(pid, {:rotate, direction, value})
  end

  @doc "Move the boat forward along its waypoint_bearing"
  def forward(pid, value) do
    GenServer.cast(pid, {:forward, value})
  end

  # Server

  @impl true
  def init(state) do
    {:ok, state}
  end

  @impl true
  def handle_call({:position}, _from, [position_e, position_n, _waypoint_e, _waypoint_n] = state) do
    {:reply, [position_e, position_n], state}
  end

  @impl true
  def handle_call({:waypoint}, _from, [_position_e, _position_n, waypoint_e, waypoint_n] = state) do
    {:reply, [waypoint_e, waypoint_n], state}
  end

  @impl true
  def handle_call({:state}, _from, state) do
    {:reply, state, state}
  end

  @impl true
  def handle_cast({:move, direction, value}, [position_e, position_n, waypoint_e, waypoint_n]) do
    [delta_e, delta_n] = direction_value(direction, value)

    {
      :noreply,
      [
        position_e + delta_e,
        position_n + delta_n,
        waypoint_e,
        waypoint_n
      ]
    }
  end

  @impl true
  def handle_cast({:move_w, direction, value}, [position_e, position_n, waypoint_e, waypoint_n]) do
    [delta_e, delta_n] = direction_value(direction, value)

    {
      :noreply,
      [
        position_e,
        position_n,
        waypoint_e + delta_e,
        waypoint_n + delta_n
      ]
    }
  end

  @impl true
  def handle_cast({:rotate, direction, value}, [position_e, position_n, waypoint_e, waypoint_n]) do
    [new_waypoint_e, new_waypoint_n] = new_waypoints([waypoint_e, waypoint_n], direction, value)

    {
      :noreply,
      [
        position_e,
        position_n,
        new_waypoint_e,
        new_waypoint_n
      ]
    }
  end

  @impl true
  def handle_cast({:forward, value}, [position_e, position_n, waypoint_e, waypoint_n]) do
    {
      :noreply,
      [
        position_e + value * waypoint_e,
        position_n + value * waypoint_n,
        waypoint_e,
        waypoint_n
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
  defp new_waypoints([waypoint_e, waypoint_n], direction, value) do
    case rotate_value(direction, value) do
      90 -> [waypoint_e, waypoint_n]
      180 -> [waypoint_n, -waypoint_e]
      270 -> [-waypoint_e, -waypoint_n]
      0 -> [-waypoint_n, waypoint_e]
    end
  end

  @doc false
  defp rotate_value(direction, value) do
    my_value =
      case direction do
        "R" -> value
        "L" -> -value
      end

    (90 + my_value)
    |> rem(360)
    |> unneg()
  end

  @doc false
  defp unneg(waypoint_bearing) when waypoint_bearing < 0 do
    waypoint_bearing + 360
  end

  @doc false
  defp unneg(waypoint_bearing) do
    waypoint_bearing
  end
end
