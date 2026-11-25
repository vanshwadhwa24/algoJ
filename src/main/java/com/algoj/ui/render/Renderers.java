package com.algoj.ui.render;

import com.algoj.catalog.AlgorithmInfo;

/** Chooses the renderer that matches an algorithm or structure. */
public final class Renderers {
    private Renderers() {}

    public static StateRenderer forInfo(AlgorithmInfo a) {
        return switch (a.id()) {
            case "arrays" -> new ArrayRenderers.Cells();
            case "linked-lists" -> new LinearRenderers.LinkedList();
            case "stacks" -> new LinearRenderers.Stack();
            case "queues" -> new LinearRenderers.Queue();
            case "hash-tables" -> new LinearRenderers.HashTable();
            case "binary-search-tree" -> new TreeRenderers.Bst();
            case "avl-trees" -> new TreeRenderers.Avl();
            case "binary-heaps" -> new ArrayRenderers.Heap();
            case "tries" -> new TreeRenderers.Trie();
            case "factorial" -> new RecursionRenderers.Factorial();
            case "fibonacci" -> new RecursionRenderers.Fibonacci();
            case "tower-of-hanoi" -> new RecursionRenderers.Hanoi();
            case "n-queens" -> new RecursionRenderers.Queens();
            case "knapsack", "lcs" -> new DpRenderer();
            default -> a.category().equals("graphs") ? new GraphRenderer() : new ArrayRenderers.Bars();
        };
    }
}
