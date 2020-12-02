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

  test "returns results on successful tripling", %{registry: registry} do
    D1.Ledger.add(registry, 20)
    D1.Ledger.add(registry, 1000)
    D1.Ledger.add(registry, 1000)
    assert D1.Ledger.result_three(registry) == {:ok, 20_000_000}
  end

  test "returns smallest result on successful tripling", %{registry: registry} do
    D1.Ledger.add(registry, 800)
    D1.Ledger.add(registry, 800)
    D1.Ledger.add(registry, 600)
    D1.Ledger.add(registry, 20)
    D1.Ledger.add(registry, 1000)
    D1.Ledger.add(registry, 1000)
    assert D1.Ledger.result_three(registry) == {:ok, 20_000_000}
  end

  test "returns results on failed tripling", %{registry: registry} do
    D1.Ledger.add(registry, 20)
    D1.Ledger.add(registry, 1000)
    D1.Ledger.add(registry, 1001)
    assert D1.Ledger.result_three(registry) == {:none, nil}
  end

  test "permutations3", %{registry: _registry} do
    assert D1.Ledger.permutations([:a, :b, :c]) == [[:a, :b, :c]]
  end

  test "permutations4", %{registry: _registry} do
    assert D1.Ledger.permutations([:a, :b, :c, :d]) == [
             [:a, :b, :c],
             [:a, :b, :d],
             [:a, :c, :d],
             [:b, :c, :d]
           ]
  end

  test "permutations5", %{registry: _registry} do
    assert D1.Ledger.permutations([:a, :b, :c, :d, :e]) == [
             [:a, :b, :c],
             [:a, :b, :d],
             [:a, :b, :e],
             [:a, :c, :d],
             [:a, :c, :e],
             [:a, :d, :e],
             [:b, :c, :d],
             [:b, :c, :e],
             [:b, :d, :e],
             [:c, :d, :e]
           ]
  end
end
