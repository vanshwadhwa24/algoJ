package com.algoj.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchModel {

    public static class SearchElement {
        private String id;
        private int value;
        private String state; // default, active, mid, eliminated, found, not-found

        public SearchElement() {}

        public SearchElement(String id, int value, String state) {
            this.id = id;
            this.value = value;
            this.state = state;
        }

        public SearchElement copy() {
            return new SearchElement(this.id, this.value, this.state);
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
    }

    public static class SearchState {
        private List<SearchElement> array = new ArrayList<>();
        private int target;
        private Integer currentIndex;
        private Integer lowIndex;
        private Integer highIndex;
        private Integer midIndex;
        private int comparisons;
        private Integer foundIndex;
        private boolean isSorted;

        public SearchState() {}

        public SearchState copy() {
            SearchState s = new SearchState();
            for (SearchElement el : this.array) {
                s.array.add(el.copy());
            }
            s.target = this.target;
            s.currentIndex = this.currentIndex;
            s.lowIndex = this.lowIndex;
            s.highIndex = this.highIndex;
            s.midIndex = this.midIndex;
            s.comparisons = this.comparisons;
            s.foundIndex = this.foundIndex;
            s.isSorted = this.isSorted;
            return s;
        }

        public List<SearchElement> getArray() { return array; }
        public void setArray(List<SearchElement> array) { this.array = array; }
        public int getTarget() { return target; }
        public void setTarget(int target) { this.target = target; }
        public Integer getCurrentIndex() { return currentIndex; }
        public void setCurrentIndex(Integer currentIndex) { this.currentIndex = currentIndex; }
        public Integer getLowIndex() { return lowIndex; }
        public void setLowIndex(Integer lowIndex) { this.lowIndex = lowIndex; }
        public Integer getHighIndex() { return highIndex; }
        public void setHighIndex(Integer highIndex) { this.highIndex = highIndex; }
        public Integer getMidIndex() { return midIndex; }
        public void setMidIndex(Integer midIndex) { this.midIndex = midIndex; }
        public int getComparisons() { return comparisons; }
        public void setComparisons(int comparisons) { this.comparisons = comparisons; }
        public Integer getFoundIndex() { return foundIndex; }
        public void setFoundIndex(Integer foundIndex) { this.foundIndex = foundIndex; }
        public boolean isSorted() { return isSorted; }
        public void setSorted(boolean sorted) { isSorted = sorted; }
    }
}
