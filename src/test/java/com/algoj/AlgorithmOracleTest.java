package com.algoj;

import com.algoj.algorithm.AlgorithmInput;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.GraphModel.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.algoj.TestSupport.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Checks each algorithm's final result against an independent reference on seeded random inputs. */
class AlgorithmOracleTest {

    private final Random random = new Random(42);

    private List<Integer> randomList(int n, int bound) {
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < n; i++) l.add(random.nextInt(bound));
        return l;
    }

    @SuppressWarnings("unchecked")
    private static List<Integer> values(Map<String, Object> state) {
        List<Integer> out = new ArrayList<>();
        for (Map<String, Object> e : (List<Map<String, Object>>) state.get("array")) out.add(((Number) e.get("value")).intValue());
        return out;
    }

    @Test
    void sortsProduceSortedPermutation() {
        Failures f = new Failures();
        for (String id : List.of("bubble-sort", "selection-sort", "insertion-sort", "merge-sort", "quick-sort", "heap-sort")) {
            for (int i = 0; i < 200; i++) {
                List<Integer> in = randomList(1 + random.nextInt(12), 15);
                List<Integer> expected = new ArrayList<>(in);
                Collections.sort(expected);
                List<Integer> got = values(last(run(id, AlgorithmInput.ofArray(in, null))));
                if (!got.equals(expected)) f.add(id, in + " -> " + got);
            }
        }
        assertTrue(f.isEmpty(), f.toString());
    }

    @Test
    void searchesFindTheRightIndex() {
        Failures f = new Failures();
        for (int i = 0; i < 300; i++) {
            List<Integer> in = randomList(1 + random.nextInt(10), 12);
            int target = random.nextInt(14);
            Object found = last(run("linear-search", AlgorithmInput.ofArray(in, target))).get("foundIndex");
            int got = found == null ? -1 : ((Number) found).intValue();
            if (got != in.indexOf(target)) f.add("linear-search", in + " target " + target + " got " + got);

            List<Integer> sorted = new ArrayList<>(in);
            Collections.sort(sorted);
            found = last(run("binary-search", AlgorithmInput.ofArray(sorted, target))).get("foundIndex");
            got = found == null ? -1 : ((Number) found).intValue();
            if (sorted.contains(target) != (got >= 0) || (got >= 0 && sorted.get(got) != target)) {
                f.add("binary-search", sorted + " target " + target + " got " + got);
            }
        }
        assertTrue(f.isEmpty(), f.toString());
    }

    private GraphData randomGraph(boolean directed, boolean allowNegative) {
        int n = 2 + random.nextInt(6);
        List<GraphVertex> vs = new ArrayList<>();
        for (int i = 0; i < n; i++) vs.add(new GraphVertex("" + (char) ('A' + i), "" + (char) ('A' + i), 50 * i, 50));
        List<GraphEdge> es = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (int i = 0, m = random.nextInt(2 * n + 1); i < m; i++) {
            int u = random.nextInt(n), v = random.nextInt(n);
            if (u == v) continue;
            String key = directed ? u + "," + v : Math.min(u, v) + "," + Math.max(u, v);
            if (!seen.add(key)) continue;
            int w = allowNegative ? random.nextInt(9) - 2 : 1 + random.nextInt(9);
            es.add(new GraphEdge("e" + i, vs.get(u).getId(), vs.get(v).getId(), w, directed));
        }
        GraphData g = new GraphData();
        g.setVertices(vs);
        g.setEdges(es);
        g.setDirected(directed);
        g.setWeighted(true);
        g.setStartVertexId("A");
        return g;
    }

    private static double[][] floyd(GraphData g) {
        int n = g.getVertices().size();
        double[][] d = new double[n][n];
        for (double[] row : d) Arrays.fill(row, Double.POSITIVE_INFINITY);
        for (int i = 0; i < n; i++) d[i][i] = 0;
        for (GraphEdge e : g.getEdges()) {
            int u = e.getSource().charAt(0) - 'A', v = e.getTarget().charAt(0) - 'A';
            d[u][v] = Math.min(d[u][v], e.getWeight());
            if (!g.isDirected()) d[v][u] = Math.min(d[v][u], e.getWeight());
        }
        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++) d[i][j] = Math.min(d[i][j], d[i][k] + d[k][j]);
        return d;
    }

    private static String describe(GraphData g) {
        StringBuilder b = new StringBuilder(g.isDirected() ? "directed " : "undirected ");
        for (GraphEdge e : g.getEdges()) b.append(e.getSource()).append("-").append(e.getTarget()).append(":").append(e.getWeight()).append(' ');
        return b.toString();
    }

    private static AlgorithmInput graphInput(GraphData g) {
        return new AlgorithmInput(null, null, null, null, null, null, null, g);
    }

    @SuppressWarnings("unchecked")
    @Test
    void graphAlgorithmsMatchReferenceResults() {
        Failures f = new Failures();
        for (int it = 0; it < 300; it++) {
            boolean directed = random.nextBoolean();
            boolean negative = directed && random.nextInt(3) == 0;
            GraphData g = randomGraph(directed, negative);
            int n = g.getVertices().size();
            double[][] d = floyd(g);
            boolean negCycleReachable = false, anyNegCycle = false;
            for (int i = 0; i < n; i++) {
                if (d[i][i] < 0) {
                    anyNegCycle = true;
                    if (d[0][i] < Double.POSITIVE_INFINITY) negCycleReachable = true;
                }
            }
            Set<Integer> reach = new HashSet<>();
            for (int i = 0; i < n; i++) if (d[0][i] < Double.POSITIVE_INFINITY) reach.add(i);
            String desc = describe(g);
            // Start vertex is the graph's first vertex "A" (target null => first vertex).
            AlgorithmInput in = graphInput(g);

            for (String id : List.of("bfs", "dfs")) {
                List<String> order = (List<String>) last(run(id, in)).get("traversalOrder");
                Set<Integer> got = new HashSet<>();
                for (String s : order) got.add(s.charAt(0) - 'A');
                if (!got.equals(reach) || order.size() != got.size()) f.add(id, desc + " got " + order);
            }
            if (!negative) {
                Map<String, Object> dist = (Map<String, Object>) last(run("dijkstra", in)).get("distances");
                for (int i = 0; i < n; i++) {
                    if (num(dist.get("" + (char) ('A' + i))) != d[0][i]) {
                        f.add("dijkstra", desc + " vertex " + (char) ('A' + i));
                        break;
                    }
                }
            }
            Map<String, Object> bf = last(run("bellman-ford", in));
            boolean detected = Boolean.TRUE.equals(bf.get("hasNegativeWeightCycle"));
            if (detected != negCycleReachable) {
                f.add("bellman-ford-negative-cycle", desc + " detected=" + detected + " expected=" + negCycleReachable);
            } else if (!negCycleReachable) {
                Map<String, Object> dist = (Map<String, Object>) bf.get("distances");
                for (int i = 0; i < n; i++) {
                    if (num(dist.get("" + (char) ('A' + i))) != d[0][i]) {
                        f.add("bellman-ford", desc + " vertex " + (char) ('A' + i));
                        break;
                    }
                }
            }
            if (!anyNegCycle) {
                Map<String, Object> matrix = (Map<String, Object>) last(run("floyd-warshall", in)).get("matrix");
                List<List<Object>> dm = (List<List<Object>>) matrix.get("dist");
                outer:
                for (int i = 0; i < n; i++)
                    for (int j = 0; j < n; j++)
                        if (num(dm.get(i).get(j)) != d[i][j]) {
                            f.add("floyd-warshall", desc + " [" + i + "][" + j + "]");
                            break outer;
                        }
            }
            if (!directed) {
                // Kruskal on the whole forest; Prim only on the component of A.
                List<GraphEdge> sorted = new ArrayList<>(g.getEdges());
                sorted.sort(Comparator.comparingInt(GraphEdge::getWeight));
                int[] parent = new int[n];
                for (int i = 0; i < n; i++) parent[i] = i;
                int total = 0, componentOfA = 0;
                for (GraphEdge e : sorted) {
                    int u = find(parent, e.getSource().charAt(0) - 'A'), v = find(parent, e.getTarget().charAt(0) - 'A');
                    if (u != v) {
                        parent[u] = v;
                        total += e.getWeight();
                        if (reach.contains(e.getSource().charAt(0) - 'A')) componentOfA += e.getWeight();
                    }
                }
                int k = ((Number) last(run("kruskal", in)).get("totalWeight")).intValue();
                if (k != total) f.add("kruskal", desc + " got " + k + " expected " + total);
                int p = ((Number) last(run("prim", in)).get("totalWeight")).intValue();
                if (p != componentOfA) f.add("prim", desc + " got " + p + " expected " + componentOfA);
            }
        }
        assertTrue(f.isEmpty(), f.toString());
    }

    private static int find(int[] p, int x) {
        while (p[x] != x) x = p[x] = p[p[x]];
        return x;
    }

    @Test
    void recursionAndDynamicProgrammingMatchReferences() {
        Failures f = new Failures();
        long factorial = 1;
        for (int n = 0; n <= 7; n++) {
            if (n > 0) factorial *= n;
            long got = ((Number) last(run("factorial", new AlgorithmInput(null, null, n, null, null, null, null, null))).get("result")).longValue();
            if (got != factorial) f.add("factorial", "n=" + n + " got " + got);
        }
        int[] fib = new int[11];
        fib[1] = 1;
        for (int i = 2; i <= 10; i++) fib[i] = fib[i - 1] + fib[i - 2];
        for (String mode : List.of("recursive", "memoized", "tabulated")) {
            for (int n = 0; n <= (mode.equals("recursive") ? 6 : 10); n++) {
                int got = ((Number) last(run("fibonacci", new AlgorithmInput(null, null, n, mode, null, null, null, null))).get("result")).intValue();
                if (got != fib[n]) f.add("fibonacci-" + mode, "n=" + n + " got " + got);
            }
        }
        for (int disks = 1; disks <= 5; disks++) {
            Map<String, Object> s = last(run("tower-of-hanoi", new AlgorithmInput(null, null, null, null, disks, null, null, null)));
            @SuppressWarnings("unchecked") Map<String, List<Integer>> pegs = (Map<String, List<Integer>>) s.get("pegs");
            if (((Number) s.get("moveCount")).intValue() != (1 << disks) - 1 || pegs.get("C").size() != disks) f.add("hanoi", "disks=" + disks);
        }
        int[] queens = {0, 0, 0, 0, 2, 10, 4};
        for (int n = 4; n <= 6; n++) {
            int got = ((Number) last(run("n-queens", new AlgorithmInput(null, null, n, null, null, null, null, null))).get("solutionsFound")).intValue();
            if (got != queens[n]) f.add("n-queens", "n=" + n + " got " + got);
        }
        int[] w = {1, 2, 3, 4}, v = {3, 4, 5, 7};
        for (int cap = 1; cap <= 12; cap++) {
            int[] dp = new int[cap + 1];
            for (int i = 0; i < 4; i++) for (int j = cap; j >= w[i]; j--) dp[j] = Math.max(dp[j], dp[j - w[i]] + v[i]);
            int got = ((Number) last(run("knapsack", new AlgorithmInput(null, null, cap, null, null, null, null, null))).get("optimalValue")).intValue();
            if (got != dp[cap]) f.add("knapsack", "cap=" + cap + " got " + got + " expected " + dp[cap]);
        }
        for (int i = 0; i < 200; i++) {
            String a = randomText(1 + random.nextInt(6)), b = randomText(1 + random.nextInt(6));
            int[][] t = new int[a.length() + 1][b.length() + 1];
            for (int x = 1; x <= a.length(); x++)
                for (int y = 1; y <= b.length(); y++)
                    t[x][y] = a.charAt(x - 1) == b.charAt(y - 1) ? t[x - 1][y - 1] + 1 : Math.max(t[x - 1][y], t[x][y - 1]);
            String got = String.valueOf(last(run("lcs", new AlgorithmInput(null, null, null, null, null, a, b, null))).get("lcsResult"));
            if (got.length() != t[a.length()][b.length()] || !isSubsequence(got, a) || !isSubsequence(got, b)) f.add("lcs", a + "," + b + " got " + got);
        }
        assertTrue(f.isEmpty(), f.toString());
    }

    private String randomText(int n) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < n; i++) b.append("ABC".charAt(random.nextInt(3)));
        return b.toString();
    }

    private static boolean isSubsequence(String s, String t) {
        int i = 0;
        for (char c : t.toCharArray()) if (i < s.length() && s.charAt(i) == c) i++;
        return i == s.length();
    }

    /** Regression: Bellman-Ford used to relax only source-to-target on undirected edges. */
    @SuppressWarnings("unchecked")
    @Test
    void bellmanFordTreatsUndirectedEdgesAsTwoWay() {
        GraphData g = new GraphData();
        g.setVertices(List.of(new GraphVertex("A", "A", 0, 0), new GraphVertex("B", "B", 0, 0), new GraphVertex("C", "C", 0, 0)));
        g.setEdges(List.of(new GraphEdge("e1", "C", "A", 5, false), new GraphEdge("e2", "B", "C", 1, false)));
        g.setDirected(false);
        g.setStartVertexId("A");
        Map<String, Object> dist = (Map<String, Object>) last(run("bellman-ford", graphInput(g))).get("distances");
        org.junit.jupiter.api.Assertions.assertEquals(5.0, num(dist.get("C")));
        org.junit.jupiter.api.Assertions.assertEquals(6.0, num(dist.get("B")));
    }
}
