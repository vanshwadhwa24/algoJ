package com.algoj.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CatalogController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "AlgoJ Java Spring Boot Backend",
                "version", "1.0.0",
                "javaVersion", System.getProperty("java.version"),
                "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/catalog")
    public ResponseEntity<Map<String, Object>> catalog() {
        return ResponseEntity.ok(Map.of(
                "sorting", List.of("bubble-sort", "selection-sort", "insertion-sort", "merge-sort", "quick-sort", "heap-sort"),
                "searching", List.of("linear-search", "binary-search"),
                "linear", List.of("arrays", "linked-lists", "stacks", "queues", "hash-tables"),
                "trees-heaps", List.of("binary-search-tree", "avl-trees", "binary-heaps", "tries"),
                "graphs", List.of("bfs", "dfs", "dijkstra", "bellman-ford", "floyd-warshall", "kruskal", "prim"),
                "recursion", List.of("factorial", "fibonacci", "tower-of-hanoi", "n-queens"),
                "dynamic-programming", List.of("knapsack", "lcs")
        ));
    }
}
