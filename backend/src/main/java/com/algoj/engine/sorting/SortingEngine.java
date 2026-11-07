package com.algoj.engine.sorting;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.SortModel.SortElement;
import com.algoj.model.SortModel.SortState;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SortingEngine {

    private List<SortElement> createSortElements(List<Integer> values) {
        List<SortElement> list = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            list.add(new SortElement("item-" + i + "-" + values.get(i), values.get(i), "default"));
        }
        return list;
    }

    private Map<String, Integer> makeCounters(SortState s) {
        Map<String, Integer> c = new HashMap<>();
        c.put("comparisons", s.getComparisons());
        c.put("swaps", s.getSwaps());
        c.put("writes", s.getWrites());
        return c;
    }

    // ==========================================
    // 1. BUBBLE SORT
    // ==========================================
    public List<ExecutionStep> bubbleSort(List<Integer> initialValues) {
        List<ExecutionStep> steps = new ArrayList<>();
        SortState state = new SortState();
        state.setArray(createSortElements(initialValues));

        steps.add(new ExecutionStep(
                steps.size(), "init", "Initial unsorted array loaded.", 1,
                state.copy(), makeCounters(state), null
        ));

        int n = state.getArray().size();
        for (int i = 0; i < n - 1; i++) {
            boolean swappedInPass = false;
            for (int j = 0; j < n - i - 1; j++) {
                state.setComparingIndices(List.of(j, j + 1));
                state.setSwappingIndices(Collections.emptyList());
                state.getArray().get(j).setState("comparing");
                state.getArray().get(j + 1).setState("comparing");
                state.setComparisons(state.getComparisons() + 1);

                steps.add(new ExecutionStep(
                        steps.size(), "compare",
                        String.format("Comparing index %d (%d) and %d (%d).",
                                j, state.getArray().get(j).getValue(),
                                j + 1, state.getArray().get(j + 1).getValue()),
                        4, state.copy(), makeCounters(state), null
                ));

                if (state.getArray().get(j).getValue() > state.getArray().get(j + 1).getValue()) {
                    state.setSwappingIndices(List.of(j, j + 1));
                    state.getArray().get(j).setState("swapping");
                    state.getArray().get(j + 1).setState("swapping");

                    SortElement temp = state.getArray().get(j);
                    state.getArray().set(j, state.getArray().get(j + 1));
                    state.getArray().set(j + 1, temp);

                    state.setSwaps(state.getSwaps() + 1);
                    swappedInPass = true;

                    steps.add(new ExecutionStep(
                            steps.size(), "swap",
                            String.format("Swapped %d and %d because %d > %d.",
                                    state.getArray().get(j).getValue(), state.getArray().get(j + 1).getValue(),
                                    state.getArray().get(j + 1).getValue(), state.getArray().get(j).getValue()),
                            5, state.copy(), makeCounters(state), null
                    ));
                }

                state.getArray().get(j).setState("default");
                state.getArray().get(j + 1).setState("default");
                state.setComparingIndices(Collections.emptyList());
                state.setSwappingIndices(Collections.emptyList());
            }

            int settledIdx = n - i - 1;
            state.getSortedIndices().add(settledIdx);
            state.getArray().get(settledIdx).setState("sorted");

            steps.add(new ExecutionStep(
                    steps.size(), "settle",
                    String.format("Element %d is now settled at final sorted position %d.",
                            state.getArray().get(settledIdx).getValue(), settledIdx),
                    2, state.copy(), makeCounters(state), null
            ));

            if (!swappedInPass) {
                for (int k = 0; k < settledIdx; k++) {
                    if (!state.getSortedIndices().contains(k)) {
                        state.getSortedIndices().add(k);
                        state.getArray().get(k).setState("sorted");
                    }
                }
                steps.add(new ExecutionStep(
                        steps.size(), "early-exit",
                        "No swaps occurred in this pass. Array is fully sorted (Early termination O(n)).",
                        6, state.copy(), makeCounters(state), null
                ));
                break;
            }
        }

        for (int k = 0; k < n; k++) {
            if (!state.getSortedIndices().contains(k)) {
                state.getSortedIndices().add(k);
                state.getArray().get(k).setState("sorted");
            }
        }

        steps.add(new ExecutionStep(
                steps.size(), "complete",
                String.format("Bubble Sort completed! Total comparisons: %d, Swaps: %d.",
                        state.getComparisons(), state.getSwaps()),
                1, state.copy(), makeCounters(state), null
        ));

        return steps;
    }

    // ==========================================
    // 2. SELECTION SORT
    // ==========================================
    public List<ExecutionStep> selectionSort(List<Integer> initialValues) {
        List<ExecutionStep> steps = new ArrayList<>();
        SortState state = new SortState();
        state.setArray(createSortElements(initialValues));

        steps.add(new ExecutionStep(
                steps.size(), "init", "Selection Sort initialized.", 1,
                state.copy(), makeCounters(state), null
        ));

        int n = state.getArray().size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            state.getArray().get(minIdx).setState("pivot");

            steps.add(new ExecutionStep(
                    steps.size(), "select-start",
                    String.format("Starting scan for minimum element in unsorted range [%d..%d]. Initial candidate: %d.",
                            i, n - 1, state.getArray().get(minIdx).getValue()),
                    3, state.copy(), makeCounters(state), null
            ));

            for (int j = i + 1; j < n; j++) {
                state.setComparingIndices(List.of(minIdx, j));
                state.getArray().get(j).setState("comparing");
                state.setComparisons(state.getComparisons() + 1);

                steps.add(new ExecutionStep(
                        steps.size(), "compare",
                        String.format("Comparing current min %d (idx %d) with %d (idx %d).",
                                state.getArray().get(minIdx).getValue(), minIdx,
                                state.getArray().get(j).getValue(), j),
                        5, state.copy(), makeCounters(state), null
                ));

                if (state.getArray().get(j).getValue() < state.getArray().get(minIdx).getValue()) {
                    if (minIdx != i) {
                        state.getArray().get(minIdx).setState("default");
                    }
                    minIdx = j;
                    state.getArray().get(minIdx).setState("pivot");

                    steps.add(new ExecutionStep(
                            steps.size(), "new-min",
                            String.format("Found new smaller element %d at index %d.",
                                    state.getArray().get(minIdx).getValue(), minIdx),
                            5, state.copy(), makeCounters(state), null
                    ));
                } else {
                    state.getArray().get(j).setState("default");
                }
                state.setComparingIndices(Collections.emptyList());
            }

            if (minIdx != i) {
                state.setSwappingIndices(List.of(i, minIdx));
                state.getArray().get(i).setState("swapping");
                state.getArray().get(minIdx).setState("swapping");

                SortElement temp = state.getArray().get(i);
                state.getArray().set(i, state.getArray().get(minIdx));
                state.getArray().set(minIdx, temp);

                state.setSwaps(state.getSwaps() + 1);

                steps.add(new ExecutionStep(
                        steps.size(), "swap",
                        String.format("Swapped minimum %d into sorted boundary index %d.",
                                state.getArray().get(i).getValue(), i),
                        7, state.copy(), makeCounters(state), null
                ));
            }

            state.getArray().get(minIdx).setState("default");
            state.getArray().get(i).setState("sorted");
            state.getSortedIndices().add(i);
            state.setSwappingIndices(Collections.emptyList());
        }

        if (n > 0) {
            state.getArray().get(n - 1).setState("sorted");
            if (!state.getSortedIndices().contains(n - 1)) state.getSortedIndices().add(n - 1);
        }

        steps.add(new ExecutionStep(
                steps.size(), "complete",
                String.format("Selection Sort completed! Total comparisons: %d, Swaps: %d.",
                        state.getComparisons(), state.getSwaps()),
                1, state.copy(), makeCounters(state), null
        ));

        return steps;
    }

    // ==========================================
    // 3. INSERTION SORT
    // ==========================================
    public List<ExecutionStep> insertionSort(List<Integer> initialValues) {
        List<ExecutionStep> steps = new ArrayList<>();
        SortState state = new SortState();
        state.setArray(createSortElements(initialValues));

        int n = state.getArray().size();
        if (n > 0) {
            state.getArray().get(0).setState("sorted");
            state.getSortedIndices().add(0);
        }

        steps.add(new ExecutionStep(
                steps.size(), "init", "Insertion Sort initialized. Index 0 considered trivially sorted.", 1,
                state.copy(), makeCounters(state), null
        ));

        for (int i = 1; i < n; i++) {
            SortElement key = state.getArray().get(i).copy();
            state.getArray().get(i).setState("pivot");
            state.setPivotIndex(i);

            steps.add(new ExecutionStep(
                    steps.size(), "select-key",
                    String.format("Selected key %d at index %d to insert into sorted prefix [0..%d].",
                            key.getValue(), i, i - 1),
                    3, state.copy(), makeCounters(state), null
            ));

            int j = i - 1;
            while (j >= 0) {
                state.setComparingIndices(List.of(j, j + 1));
                state.getArray().get(j).setState("comparing");
                state.setComparisons(state.getComparisons() + 1);

                steps.add(new ExecutionStep(
                        steps.size(), "compare",
                        String.format("Comparing key %d with sorted prefix element %d at index %d.",
                                key.getValue(), state.getArray().get(j).getValue(), j),
                        4, state.copy(), makeCounters(state), null
                ));

                if (state.getArray().get(j).getValue() > key.getValue()) {
                    state.getArray().set(j + 1, state.getArray().get(j).copy());
                    state.getArray().get(j + 1).setState("swapping");
                    state.setWrites(state.getWrites() + 1);

                    steps.add(new ExecutionStep(
                            steps.size(), "shift",
                            String.format("Shifted %d one position right to index %d.",
                                    state.getArray().get(j + 1).getValue(), j + 1),
                            5, state.copy(), makeCounters(state), null
                    ));

                    state.getArray().get(j + 1).setState("sorted");
                    state.getArray().get(j).setState("default");
                    j--;
                } else {
                    state.getArray().get(j).setState("sorted");
                    break;
                }
            }

            state.getArray().set(j + 1, key);
            state.getArray().get(j + 1).setState("sorted");
            state.setWrites(state.getWrites() + 1);

            for (int k = 0; k <= i; k++) {
                if (!state.getSortedIndices().contains(k)) state.getSortedIndices().add(k);
                state.getArray().get(k).setState("sorted");
            }
            state.setComparingIndices(Collections.emptyList());
            state.setPivotIndex(null);

            steps.add(new ExecutionStep(
                    steps.size(), "inserted",
                    String.format("Inserted key %d into position %d. Prefix [0..%d] is now sorted.",
                            key.getValue(), j + 1, i),
                    6, state.copy(), makeCounters(state), null
            ));
        }

        steps.add(new ExecutionStep(
                steps.size(), "complete",
                String.format("Insertion Sort completed! Comparisons: %d, Writes/Shifts: %d.",
                        state.getComparisons(), state.getWrites()),
                1, state.copy(), makeCounters(state), null
        ));

        return steps;
    }

    // ==========================================
    // 4. MERGE SORT
    // ==========================================
    public List<ExecutionStep> mergeSort(List<Integer> initialValues) {
        List<ExecutionStep> steps = new ArrayList<>();
        SortState state = new SortState();
        state.setArray(createSortElements(initialValues));

        steps.add(new ExecutionStep(
                steps.size(), "init", "Merge Sort initialized. Starting divide-and-conquer.", 1,
                state.copy(), makeCounters(state), null
        ));

        runMergeSort(state, 0, state.getArray().size() - 1, steps);

        for (int i = 0; i < state.getArray().size(); i++) {
            state.getArray().get(i).setState("sorted");
            if (!state.getSortedIndices().contains(i)) state.getSortedIndices().add(i);
        }

        steps.add(new ExecutionStep(
                steps.size(), "complete",
                String.format("Merge Sort completed! Comparisons: %d, Writes: %d.",
                        state.getComparisons(), state.getWrites()),
                1, state.copy(), makeCounters(state), null
        ));

        return steps;
    }

    private void runMergeSort(SortState state, int left, int right, List<ExecutionStep> steps) {
        if (left >= right) return;

        int mid = left + (right - left) / 2;

        steps.add(new ExecutionStep(
                steps.size(), "split",
                String.format("Divided subarray [%d..%d] into [%d..%d] and [%d..%d].",
                        left, right, left, mid, mid + 1, right),
                3, state.copy(), makeCounters(state), null
        ));

        runMergeSort(state, left, mid, steps);
        runMergeSort(state, mid + 1, right, steps);
        runMerge(state, left, mid, right, steps);
    }

    private void runMerge(SortState state, int left, int mid, int right, List<ExecutionStep> steps) {
        List<SortElement> aux = new ArrayList<>();
        for (int i = left; i <= right; i++) {
            SortElement el = state.getArray().get(i).copy();
            el.setState("auxiliary");
            aux.add(el);
        }
        state.setAuxiliaryArray(aux);

        steps.add(new ExecutionStep(
                steps.size(), "merge-start",
                String.format("Merging sorted runs [%d..%d] and [%d..%d] using auxiliary buffer.",
                        left, mid, mid + 1, right),
                5, state.copy(), makeCounters(state), null
        ));

        int i = left;
        int j = mid + 1;
        List<SortElement> merged = new ArrayList<>();

        while (i <= mid && j <= right) {
            state.setComparingIndices(List.of(i, j));
            state.setComparisons(state.getComparisons() + 1);

            int valI = state.getArray().get(i).getValue();
            int valJ = state.getArray().get(j).getValue();

            steps.add(new ExecutionStep(
                    steps.size(), "merge-compare",
                    String.format("Comparing left element %d (idx %d) with right element %d (idx %d).",
                            valI, i, valJ, j),
                    6, state.copy(), makeCounters(state), null
            ));

            if (valI <= valJ) {
                merged.add(state.getArray().get(i).copy());
                i++;
            } else {
                merged.add(state.getArray().get(j).copy());
                j++;
            }
        }

        while (i <= mid) {
            merged.add(state.getArray().get(i).copy());
            i++;
        }
        while (j <= right) {
            merged.add(state.getArray().get(j).copy());
            j++;
        }

        for (int k = 0; k < merged.size(); k++) {
            int writeIdx = left + k;
            SortElement item = merged.get(k);
            item.setState("sorted");
            state.getArray().set(writeIdx, item);
            state.setWrites(state.getWrites() + 1);

            steps.add(new ExecutionStep(
                    steps.size(), "merge-write",
                    String.format("Wrote sorted element %d back to main array at index %d.",
                            item.getValue(), writeIdx),
                    7, state.copy(), makeCounters(state), null
            ));
        }

        state.setAuxiliaryArray(null);
        state.setComparingIndices(Collections.emptyList());
    }

    // ==========================================
    // 5. QUICK SORT
    // ==========================================
    public List<ExecutionStep> quickSort(List<Integer> initialValues) {
        List<ExecutionStep> steps = new ArrayList<>();
        SortState state = new SortState();
        state.setArray(createSortElements(initialValues));

        steps.add(new ExecutionStep(
                steps.size(), "init", "Quick Sort initialized (Lomuto partition scheme).", 1,
                state.copy(), makeCounters(state), null
        ));

        runQuickSort(state, 0, state.getArray().size() - 1, steps);

        for (int i = 0; i < state.getArray().size(); i++) {
            state.getArray().get(i).setState("sorted");
            if (!state.getSortedIndices().contains(i)) state.getSortedIndices().add(i);
        }

        steps.add(new ExecutionStep(
                steps.size(), "complete",
                String.format("Quick Sort completed! Comparisons: %d, Swaps: %d.",
                        state.getComparisons(), state.getSwaps()),
                1, state.copy(), makeCounters(state), null
        ));

        return steps;
    }

    private void runQuickSort(SortState state, int low, int high, List<ExecutionStep> steps) {
        if (low < high) {
            int pIndex = lomutoPartition(state, low, high, steps);
            runQuickSort(state, low, pIndex - 1, steps);
            runQuickSort(state, pIndex + 1, high, steps);
        } else if (low == high) {
            state.getArray().get(low).setState("sorted");
            if (!state.getSortedIndices().contains(low)) state.getSortedIndices().add(low);
        }
    }

    private int lomutoPartition(SortState state, int low, int high, List<ExecutionStep> steps) {
        int pivotVal = state.getArray().get(high).getValue();
        state.getArray().get(high).setState("pivot");
        state.setPivotIndex(high);

        steps.add(new ExecutionStep(
                steps.size(), "set-pivot",
                String.format("Chosen pivot %d at index %d for partition range [%d..%d].",
                        pivotVal, high, low, high),
                3, state.copy(), makeCounters(state), null
        ));

        int i = low - 1;

        for (int j = low; j < high; j++) {
            state.setComparingIndices(List.of(j, high));
            state.getArray().get(j).setState("comparing");
            state.setComparisons(state.getComparisons() + 1);

            steps.add(new ExecutionStep(
                    steps.size(), "compare",
                    String.format("Comparing index %d (%d) with pivot %d.",
                            j, state.getArray().get(j).getValue(), pivotVal),
                    5, state.copy(), makeCounters(state), null
            ));

            if (state.getArray().get(j).getValue() <= pivotVal) {
                i++;
                if (i != j) {
                    state.setSwappingIndices(List.of(i, j));
                    state.getArray().get(i).setState("swapping");
                    state.getArray().get(j).setState("swapping");

                    SortElement temp = state.getArray().get(i);
                    state.getArray().set(i, state.getArray().get(j));
                    state.getArray().set(j, temp);
                    state.setSwaps(state.getSwaps() + 1);

                    steps.add(new ExecutionStep(
                            steps.size(), "swap",
                            String.format("Swapped %d (idx %d) into smaller-partition at index %d.",
                                    state.getArray().get(i).getValue(), j, i),
                            6, state.copy(), makeCounters(state), null
                    ));
                }
            }

            state.getArray().get(j).setState("default");
            if (i >= low) state.getArray().get(i).setState("partition");
            state.setComparingIndices(Collections.emptyList());
            state.setSwappingIndices(Collections.emptyList());
        }

        // Place pivot into i + 1
        int pivotFinalIdx = i + 1;
        state.setSwappingIndices(List.of(pivotFinalIdx, high));
        state.getArray().get(pivotFinalIdx).setState("swapping");
        state.getArray().get(high).setState("swapping");

        SortElement temp = state.getArray().get(pivotFinalIdx);
        state.getArray().set(pivotFinalIdx, state.getArray().get(high));
        state.getArray().set(high, temp);
        state.setSwaps(state.getSwaps() + 1);

        state.getArray().get(pivotFinalIdx).setState("sorted");
        state.getSortedIndices().add(pivotFinalIdx);
        state.setPivotIndex(null);
        state.setSwappingIndices(Collections.emptyList());

        for (int k = low; k <= high; k++) {
            if (k != pivotFinalIdx && !state.getSortedIndices().contains(k)) {
                state.getArray().get(k).setState("default");
            }
        }

        steps.add(new ExecutionStep(
                steps.size(), "pivot-placed",
                String.format("Pivot %d placed into final sorted position %d.", pivotVal, pivotFinalIdx),
                8, state.copy(), makeCounters(state), null
        ));

        return pivotFinalIdx;
    }

    // ==========================================
    // 6. HEAP SORT
    // ==========================================
    public List<ExecutionStep> heapSort(List<Integer> initialValues) {
        List<ExecutionStep> steps = new ArrayList<>();
        SortState state = new SortState();
        state.setArray(createSortElements(initialValues));

        steps.add(new ExecutionStep(
                steps.size(), "init", "Heap Sort initialized. Building max heap.", 1,
                state.copy(), makeCounters(state), null
        ));

        int n = state.getArray().size();

        // 1. Build max heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(state, n, i, steps);
        }

        steps.add(new ExecutionStep(
                steps.size(), "heap-built", "Max heap structure built! Largest element is at index 0.", 3,
                state.copy(), makeCounters(state), null
        ));

        // 2. Extract elements one by one
        for (int i = n - 1; i > 0; i--) {
            state.setSwappingIndices(List.of(0, i));
            state.getArray().get(0).setState("swapping");
            state.getArray().get(i).setState("swapping");

            SortElement temp = state.getArray().get(0);
            state.getArray().set(0, state.getArray().get(i));
            state.getArray().set(i, temp);
            state.setSwaps(state.getSwaps() + 1);

            state.getArray().get(i).setState("sorted");
            state.getSortedIndices().add(i);

            steps.add(new ExecutionStep(
                    steps.size(), "extract-max",
                    String.format("Swapped max element %d to end at sorted position %d.", temp.getValue(), i),
                    5, state.copy(), makeCounters(state), null
            ));

            state.setSwappingIndices(Collections.emptyList());
            heapify(state, i, 0, steps);
        }

        if (n > 0) {
            state.getArray().get(0).setState("sorted");
            if (!state.getSortedIndices().contains(0)) state.getSortedIndices().add(0);
        }

        steps.add(new ExecutionStep(
                steps.size(), "complete",
                String.format("Heap Sort completed! Comparisons: %d, Swaps: %d.",
                        state.getComparisons(), state.getSwaps()),
                1, state.copy(), makeCounters(state), null
        ));

        return steps;
    }

    private void heapify(SortState state, int n, int i, List<ExecutionStep> steps) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n) {
            state.setComparingIndices(List.of(left, largest));
            state.setComparisons(state.getComparisons() + 1);
            if (state.getArray().get(left).getValue() > state.getArray().get(largest).getValue()) {
                largest = left;
            }
        }

        if (right < n) {
            state.setComparingIndices(List.of(right, largest));
            state.setComparisons(state.getComparisons() + 1);
            if (state.getArray().get(right).getValue() > state.getArray().get(largest).getValue()) {
                largest = right;
            }
        }

        if (largest != i) {
            state.setSwappingIndices(List.of(i, largest));
            state.getArray().get(i).setState("swapping");
            state.getArray().get(largest).setState("swapping");

            SortElement temp = state.getArray().get(i);
            state.getArray().set(i, state.getArray().get(largest));
            state.getArray().set(largest, temp);
            state.setSwaps(state.getSwaps() + 1);

            steps.add(new ExecutionStep(
                    steps.size(), "heapify-swap",
                    String.format("Heapify: Swapped parent %d with larger child %d.",
                            temp.getValue(), state.getArray().get(i).getValue()),
                    6, state.copy(), makeCounters(state), null
            ));

            state.getArray().get(i).setState("default");
            state.getArray().get(largest).setState("default");
            state.setComparingIndices(Collections.emptyList());
            state.setSwappingIndices(Collections.emptyList());

            heapify(state, n, largest, steps);
        }
    }
}
