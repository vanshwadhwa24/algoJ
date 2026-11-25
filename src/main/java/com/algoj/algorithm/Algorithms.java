package com.algoj.algorithm;

import com.algoj.algorithm.dp.Knapsack;
import com.algoj.algorithm.dp.Lcs;
import com.algoj.algorithm.graphs.*;
import com.algoj.algorithm.recursion.*;
import com.algoj.algorithm.searching.*;
import com.algoj.algorithm.sorting.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Registry of every algorithm, looked up by id. Adding an algorithm means adding a class and one line here. */
public final class Algorithms {

    private static final Map<String, Algorithm> BY_ID = new LinkedHashMap<>();

    static {
        List.<Algorithm>of(
                new BubbleSort(), new SelectionSort(), new InsertionSort(), new MergeSort(), new QuickSort(), new HeapSort(),
                new LinearSearch(), new BinarySearch(),
                new Bfs(), new Dfs(), new Dijkstra(), new BellmanFord(), new FloydWarshall(), new Kruskal(), new Prim(),
                new Factorial(), new Fibonacci(), new TowerOfHanoi(), new NQueens(),
                new Knapsack(), new Lcs()
        ).forEach(a -> BY_ID.put(a.id(), a));
    }

    private Algorithms() {}

    public static Algorithm get(String id) {
        Algorithm a = BY_ID.get(id);
        if (a == null) throw new IllegalArgumentException("Unsupported algorithm ID: " + id);
        return a;
    }

    public static boolean exists(String id) {
        return BY_ID.containsKey(id);
    }
}
