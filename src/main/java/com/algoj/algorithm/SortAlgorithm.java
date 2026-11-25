package com.algoj.algorithm;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.SortModel.SortElement;
import com.algoj.model.SortModel.SortState;
import java.util.*;

/** Template for all sorting algorithms: shared state setup and counters, subclasses supply the trace. */
public abstract class SortAlgorithm extends BaseAlgorithm {

    protected SortAlgorithm(String id) {
        super(id);
    }

    @Override
    public List<ExecutionStep> run(AlgorithmInput in) {
        return sort(in.arrayOrDefault());
    }

    protected abstract List<ExecutionStep> sort(List<Integer> values);

    protected List<SortElement> createSortElements(List<Integer> values) {
        List<SortElement> list = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            list.add(new SortElement("item-" + i + "-" + values.get(i), values.get(i), "default"));
        }
        return list;
    }

    protected Map<String, Integer> makeCounters(SortState s) {
        Map<String, Integer> c = new HashMap<>();
        c.put("comparisons", s.getComparisons());
        c.put("swaps", s.getSwaps());
        c.put("writes", s.getWrites());
        return c;
    }
}
