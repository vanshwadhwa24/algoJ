package com.algoj.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PracticeQuestion(String id, String algorithmId, String question, List<String> options, int correctIndex,
                               String explanation) {}
