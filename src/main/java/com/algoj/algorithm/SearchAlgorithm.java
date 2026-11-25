package com.algoj.algorithm;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.SearchModel.SearchElement;
import com.algoj.model.SearchModel.SearchState;
import java.util.*;

/** Template for searching algorithms: shared element setup and counters. */
public abstract class SearchAlgorithm extends BaseAlgorithm {

    protected SearchAlgorithm(String id) {
        super(id);
    }

    @Override
    public List<ExecutionStep> run(AlgorithmInput in) {
        return search(in.arrayOrDefault(), in.target() != null ? in.target() : 22);
    }

    protected abstract List<ExecutionStep> search(List<Integer> values, int target);

    protected List<SearchElement> createSearchElements(List<Integer> values) {
        List<SearchElement> list = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            list.add(new SearchElement("search-" + i + "-" + values.get(i), values.get(i), "default"));
        }
        return list;
    }

    protected Map<String, Integer> makeCounters(SearchState s) {
        Map<String, Integer> c = new HashMap<>();
        c.put("comparisons", s.getComparisons());
        return c;
    }
}
