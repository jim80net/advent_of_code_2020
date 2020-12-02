defmodule D1.LedgerTest do
  use ExUnit.Case, async: true

  setup do
    registry = start_supervised!(D1.Ledger)
    %{registry: registry}
  end

  test "results = {:none}, when empty", %{registry: registry} do
    assert D1.Ledger.result(registry, 1) == {:none, nil}
  end

  test "adds originals and complements to state", %{registry: registry} do
    D1.Ledger.add(registry, 157)
    assert D1.Ledger.originals(registry) == {:ok, [157]}
    assert D1.Ledger.complements(registry) == {:ok, [1863]}
    assert D1.Ledger.result(registry, 157) == {:none, nil}
  end

  test "returns results on successful pairing", %{registry: registry} do
    D1.Ledger.add(registry, 20)
    assert D1.Ledger.result(registry, 2000) == {:ok, 40000}
  end
end
