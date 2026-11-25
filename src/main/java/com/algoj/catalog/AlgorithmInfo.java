package com.algoj.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/** Display metadata for one algorithm or data structure: description, complexity, code listing and theory. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AlgorithmInfo(String id, String name, String category, String difficulty, String description,
                            List<String> tags, Complexity complexity, Code code, Learning learning, JsonNode defaultInput) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Complexity(String timeBest, String timeAverage, String timeWorst, String spaceAuxiliary, String explanation) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Code(String language, List<String> lines) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Learning(String intuition, List<String> howItWorks, List<String> edgeCases, List<String> advantages,
                           List<String> limitations) {}

    public boolean isSortOrSearch() {
        return category.equals("sorting") || category.equals("searching");
    }

    /** Short label used on library cards and category chips. */
    public String categoryLabel() {
        return category.toUpperCase();
    }
}
