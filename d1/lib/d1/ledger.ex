defmodule D1.Ledger do
  use GenServer

  # Client
  def start_link(_opts) do
    GenServer.start_link(__MODULE__, [[], []])
  end

  def add(pid, new_original) do
    GenServer.cast(pid, {:add, new_original})
  end

  def originals(pid) do
    GenServer.call(pid, {:originals})
  end

  def complements(pid) do
    GenServer.call(pid, {:complements})
  end

  def result(pid, test_original) do
    GenServer.call(pid, {:result, test_original})
  end

  def result_three(pid) do
    # 600 second timeout
    GenServer.call(pid, {:result_three})
  end

  # Server

  @impl true
  def init(state) do
    {:ok, state}
  end

  @impl true
  def handle_cast({:add, new_original}, [originals, complements]) do
    {
      :noreply,
      [
        [new_original | originals],
        [2020 - new_original | complements]
      ]
    }
  end

  @impl true
  def handle_call({:originals}, _from, [originals, _complements] = state) do
    {:reply, {:ok, originals}, state}
  end

  @impl true
  def handle_call({:complements}, _from, [_originals, complements] = state) do
    {:reply, {:ok, complements}, state}
  end

  @impl true
  def handle_call({:result, test_original}, _from, [originals, complements] = state) do
    index = Enum.find_index(complements, fn x -> x == test_original end)

    if index do
      result = Enum.at(originals, index) * test_original
      {:reply, {:ok, result}, state}
    else
      {:reply, {:none, nil}, state}
    end
  end

  @impl true
  def handle_call({:result_three}, _from, [originals, _complements] = state) do
    result = three_elements_add_to_2020(originals)

    case result do
      [x, y, z] -> {:reply, {:ok, x * y * z}, state}
      nil -> {:reply, {:none, nil}, state}
    end
  end

  @doc false
  def three_elements_add_to_2020(originals) do
    originals
    |> Enum.sort(:asc)
    |> permutations()
    |> Enum.find(fn [x, y, z] -> x + y + z == 2020 end)
  end

  @doc false
  def permutations(list, num \\ 3)

  @doc false
  def permutations(_list, 0), do: [[]]

  @doc false
  def permutations([], _num), do: []

  @doc false
  def permutations([head | tail], num) do
    Enum.map(permutations(tail, num - 1), &[head | &1]) ++ permutations(tail, num)
  end
end
