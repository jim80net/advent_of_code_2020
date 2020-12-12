defmodule NaviTest do
  use ExUnit.Case, async: true
  alias D12.Navi

  setup do
    registry = start_supervised!(Navi)
    %{registry: registry}
  end

  test "position == [0, 0] when initialized", %{registry: registry} do
    assert Navi.position(registry) == [0, 0]
  end

  test "moves N 10", %{registry: registry} do
    Navi.move(registry, "N", 10)
    assert Navi.position(registry) == [0, 10]
  end

  test "moves S 10", %{registry: registry} do
    Navi.move(registry, "S", 10)
    assert Navi.position(registry) == [0, -10]
  end

  test "moves E 10", %{registry: registry} do
    Navi.move(registry, "E", 10)
    assert Navi.position(registry) == [10, 0]
  end

  test "moves W 10", %{registry: registry} do
    Navi.move(registry, "W", 10)
    assert Navi.position(registry) == [-10, 0]
  end

  test "bearing", %{registry: registry} do
    assert Navi.bearing(registry) == 90
  end

  test "rotates", %{registry: registry} do
    Navi.rotate(registry, "R", 90)
    assert Navi.bearing(registry) == 180
    Navi.rotate(registry, "R", 90)
    assert Navi.bearing(registry) == 270
    Navi.rotate(registry, "R", 90)
    assert Navi.bearing(registry) == 0
    Navi.rotate(registry, "R", 720)
    assert Navi.bearing(registry) == 0
    Navi.rotate(registry, "L", 90)
    assert Navi.bearing(registry) == 270
    Navi.rotate(registry, "L", 180)
    assert Navi.bearing(registry) == 90
    Navi.rotate(registry, "L", 90)
    assert Navi.bearing(registry) == 0
    Navi.rotate(registry, "L", 90)
    assert Navi.bearing(registry) == 270
    Navi.rotate(registry, "L", 720)
    assert Navi.bearing(registry) == 270
  end

  test "forward", %{registry: registry} do
    Navi.forward(registry, 10)
    assert Navi.state(registry) == [10, 0, 90]
    Navi.rotate(registry, "R", 90)
    Navi.forward(registry, 10)
    assert Navi.state(registry) == [10, -10, 180]
    Navi.rotate(registry, "R", 90)
    Navi.forward(registry, 5)
    assert Navi.state(registry) == [5, -10, 270]
    Navi.rotate(registry, "R", 90)
    Navi.forward(registry, 5)
    assert Navi.state(registry) == [5, -5, 0]
  end
end
