package com.algoj.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SortModel {

    public static class SortElement {
        private String id;
        private int value;
        private String state; // default, comparing, swapping, sorted, pivot, partition, auxiliary

        public SortElement() {}

        public SortElement(String id, int value, String state) {
            this.id = id;
            this.value = value;
            this.state = state;
        }

        public SortElement copy() {
            return new SortElement(this.id, this.value, this.state);
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
    }

    public static class SortState {
        private List<SortElement> array = new ArrayList<>();
        private List<Integer> comparingIndices = new ArrayList<>();
        private List<Integer> swappingIndices = new ArrayList<>();
        private Integer pivotIndex;
        private List<Integer> sortedIndices = new ArrayList<>();
        private List<SortElement> auxiliaryArray;
        private int comparisons;
        private int swaps;
        private int writes;

        public SortState() {}

        public SortState copy() {
            SortState s = new SortState();
            for (SortElement el : this.array) {
                s.array.add(el.copy());
            }
            s.comparingIndices = new ArrayList<>(this.comparingIndices);
            s.swappingIndices = new ArrayList<>(this.swappingIndices);
            s.pivotIndex = this.pivotIndex;
            s.sortedIndices = new ArrayList<>(this.sortedIndices);
            if (this.auxiliaryArray != null) {
                s.auxiliaryArray = new ArrayList<>();
                for (SortElement el : this.auxiliaryArray) {
                    s.auxiliaryArray.add(el.copy());
                }
            }
            s.comparisons = this.comparisons;
            s.swaps = this.swaps;
            s.writes = this.writes;
            return s;
        }

        public List<SortElement> getArray() { return array; }
        public void setArray(List<SortElement> array) { this.array = array; }
        public List<Integer> getComparingIndices() { return comparingIndices; }
        public void setComparingIndices(List<Integer> comparingIndices) { this.comparingIndices = comparingIndices; }
        public List<Integer> getSwappingIndices() { return swappingIndices; }
        public void setSwappingIndices(List<Integer> swappingIndices) { this.swappingIndices = swappingIndices; }
        public Integer getPivotIndex() { return pivotIndex; }
        public void setPivotIndex(Integer pivotIndex) { this.pivotIndex = pivotIndex; }
        public List<Integer> getSortedIndices() { return sortedIndices; }
        public void setSortedIndices(List<Integer> sortedIndices) { this.sortedIndices = sortedIndices; }
        public List<SortElement> getAuxiliaryArray() { return auxiliaryArray; }
        public void setAuxiliaryArray(List<SortElement> auxiliaryArray) { this.auxiliaryArray = auxiliaryArray; }
        public int getComparisons() { return comparisons; }
        public void setComparisons(int comparisons) { this.comparisons = comparisons; }
        public int getSwaps() { return swaps; }
        public void setSwaps(int swaps) { this.swaps = swaps; }
        public int getWrites() { return writes; }
        public void setWrites(int writes) { this.writes = writes; }
    }
}
