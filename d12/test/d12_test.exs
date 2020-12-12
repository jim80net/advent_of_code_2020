defmodule D1Test do
  use ExUnit.Case
  doctest D12

  test "parse_action_value" do
    assert D12.parse_action_value("F10") == ["F", 10]
  end

  test "manhattan" do
    assert D12.manhattan([1, 3]) == 4
    assert D12.manhattan([1, -3]) == 4
  end
end
