defmodule Navi22Test do
  use ExUnit.Case, async: true
  alias D12.Navi2

  setup do
    registry = start_supervised!(Navi2)
    %{registry: registry}
  end

  test "position == [0, 0] when initialized", %{registry: registry} do
    assert Navi2.position(registry) == [0, 0]
  end

  test "waypoint == [10, 1] when initialized", %{registry: registry} do
    assert Navi2.waypoint(registry) == [10, 1]
  end

  test "moves boat", %{registry: registry} do
    Navi2.move(registry, "N", 10)
    assert Navi2.position(registry) == [0, 10]
    Navi2.move(registry, "S", 10)
    assert Navi2.position(registry) == [0, 0]
    Navi2.move(registry, "E", 10)
    assert Navi2.position(registry) == [10, 0]
    Navi2.move(registry, "W", 10)
    assert Navi2.position(registry) == [0, 0]
  end

  test "moves waypoint", %{registry: registry} do
    Navi2.move_w(registry, "N", 10)
    assert Navi2.waypoint(registry) == [10, 11]
    Navi2.move_w(registry, "S", 10)
    assert Navi2.waypoint(registry) == [10, 1]
    Navi2.move_w(registry, "E", 10)
    assert Navi2.waypoint(registry) == [20, 1]
    Navi2.move_w(registry, "W", 10)
    assert Navi2.waypoint(registry) == [10, 1]
  end

  test "rotates", %{registry: registry} do
    # 180
    Navi2.rotate(registry, "R", 90)
    assert Navi2.waypoint(registry) == [1, -10]
    # 270
    Navi2.rotate(registry, "R", 90)
    assert Navi2.waypoint(registry) == [-10, -1]
    # 0 / 360
    Navi2.rotate(registry, "R", 90)
    assert Navi2.waypoint(registry) == [-1, 10]
    # 0 / 360
    Navi2.rotate(registry, "R", 720)
    assert Navi2.waypoint(registry) == [-1, 10]
    # 270
    Navi2.rotate(registry, "L", 90)
    assert Navi2.waypoint(registry) == [-10, -1]
    # 90
    Navi2.rotate(registry, "L", 180)
    assert Navi2.waypoint(registry) == [10, 1]
    # 0
    Navi2.rotate(registry, "L", 90)
    assert Navi2.waypoint(registry) == [-1, 10]
    # 270
    Navi2.rotate(registry, "L", 90)
    assert Navi2.waypoint(registry) == [-10, -1]
    # 270
    Navi2.rotate(registry, "L", 720)
    assert Navi2.waypoint(registry) == [-10, -1]
  end

  test "forward", %{registry: registry} do
    Navi2.forward(registry, 1)
    assert Navi2.position(registry) == [10, 1]
    Navi2.forward(registry, 2)
    assert Navi2.position(registry) == [30, 3]
    Navi2.rotate(registry, "R", 180)
    Navi2.forward(registry, 3)
    assert Navi2.position(registry) == [0, 0]
    Navi2.forward(registry, 1)
    assert Navi2.position(registry) == [-10, -1]
  end
end
