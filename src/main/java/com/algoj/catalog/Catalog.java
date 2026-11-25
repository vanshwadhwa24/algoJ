package com.algoj.catalog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/** The algorithm catalog and practice questions, read once from JSON resources. */
public final class Catalog {

    private static final ObjectMapper JSON = new ObjectMapper();
    private final List<AlgorithmInfo> algorithms;
    private final List<PracticeQuestion> questions;

    public Catalog(List<AlgorithmInfo> algorithms, List<PracticeQuestion> questions) {
        this.algorithms = List.copyOf(algorithms);
        this.questions = List.copyOf(questions);
    }

    public static Catalog load() {
        try (InputStream a = Catalog.class.getResourceAsStream("/catalog.json");
             InputStream q = Catalog.class.getResourceAsStream("/questions.json")) {
            return new Catalog(JSON.readValue(a, new TypeReference<List<AlgorithmInfo>>() {}),
                    JSON.readValue(q, new TypeReference<List<PracticeQuestion>>() {}));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read catalog resources", e);
        }
    }

    public List<AlgorithmInfo> algorithms() {
        return algorithms;
    }

    public List<PracticeQuestion> questions() {
        return questions;
    }

    public Optional<AlgorithmInfo> find(String id) {
        return algorithms.stream().filter(a -> a.id().equals(id)).findFirst();
    }

    public List<PracticeQuestion> questionsFor(String algorithmId) {
        return questions.stream().filter(q -> q.algorithmId().equals(algorithmId)).toList();
    }
}
