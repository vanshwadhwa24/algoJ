package com.algoj.engine.graphs;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.GraphModel.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GraphAlgorithmEngine {

    public GraphData getSampleGraph() {
        GraphData g = new GraphData();
        g.setDirected(false);
        g.setWeighted(true);
        g.setStartVertexId("A");

        List<GraphVertex> vertices = List.of(
                new GraphVertex("A", "A", 120, 150),
                new GraphVertex("B", "B", 260, 70),
                new GraphVertex("C", "C", 260, 230),
                new GraphVertex("D", "D", 420, 70),
                new GraphVertex("E", "E", 420, 230),
                new GraphVertex("F", "F", 560, 150)
        );

        List<GraphEdge> edges = List.of(
                new GraphEdge("e1", "A", "B", 4, false),
                new GraphEdge("e2", "A", "C", 2, false),
                new GraphEdge("e3", "B", "C", 1, false),
                new GraphEdge("e4", "B", "D", 5, false),
                new GraphEdge("e5", "C", "E", 8, false),
                new GraphEdge("e6", "C", "D", 8, false),
                new GraphEdge("e7", "D", "E", 2, false),
                new GraphEdge("e8", "D", "F", 6, false),
                new GraphEdge("e9", "E", "F", 3, false)
        );

        g.setVertices(new ArrayList<>(vertices));
        g.setEdges(new ArrayList<>(edges));
        return g;
    }

    private Map<String, List<GraphEdge>> getAdjacencyList(GraphData graph) {
        Map<String, List<GraphEdge>> adj = new HashMap<>();
        for (GraphVertex v : graph.getVertices()) {
            adj.put(v.getId(), new ArrayList<>());
        }
        for (GraphEdge e : graph.getEdges()) {
            adj.computeIfAbsent(e.getSource(), k -> new ArrayList<>()).add(e);
            if (!graph.isDirected()) {
                GraphEdge rev = new GraphEdge(e.getId() + "-rev", e.getTarget(), e.getSource(), e.getWeight(), false);
                adj.computeIfAbsent(e.getTarget(), k -> new ArrayList<>()).add(rev);
            }
        }
        return adj;
    }

    // ==========================================
    // 1. BFS
    // ==========================================
    public List<ExecutionStep> bfs(GraphData initialGraph, String startVertexId) {
        List<ExecutionStep> steps = new ArrayList<>();
        GraphData graph = initialGraph != null ? initialGraph.copy() : getSampleGraph();
        String start = startVertexId != null ? startVertexId : graph.getVertices().get(0).getId();

        GraphTraversalState state = new GraphTraversalState();
        state.setGraph(graph);
        state.setStartVertex(start);
        state.setQueueOrStack(new ArrayList<>(List.of(start)));
        state.setVisited(new ArrayList<>(List.of(start)));
        state.setCurrentVertex(null);
        state.setMessage(String.format("BFS initialized at start vertex %s.", start));

        for (GraphVertex v : graph.getVertices()) {
            if (v.getId().equals(start)) v.setState("queued");
        }

        steps.add(new ExecutionStep(
                steps.size(), "init", state.getMessage(), 1, state.copy(),
                Map.of("visited", 1, "queueSize", 1), null
        ));

        Map<String, List<GraphEdge>> adj = getAdjacencyList(graph);
        Queue<String> queue = new LinkedList<>();
        queue.add(start);
        Set<String> visitedSet = new HashSet<>();
        visitedSet.add(start);

        while (!queue.isEmpty()) {
            String curr = queue.poll();
            state.setCurrentVertex(curr);
            state.getTraversalOrder().add(curr);
            state.setQueueOrStack(new ArrayList<>(queue));

            for (GraphVertex v : graph.getVertices()) {
                if (v.getId().equals(curr)) v.setState("current");
            }

            steps.add(new ExecutionStep(
                    steps.size(), "dequeue",
                    String.format("Dequeued vertex %s. Examining unvisited neighbors.", curr),
                    3, state.copy(),
                    Map.of("visited", visitedSet.size(), "queueSize", queue.size()), null
            ));

            List<GraphEdge> neighbors = adj.getOrDefault(curr, Collections.emptyList());
            for (GraphEdge edge : neighbors) {
                String neighborId = edge.getTarget();
                if (!visitedSet.contains(neighborId)) {
                    visitedSet.add(neighborId);
                    queue.add(neighborId);
                    state.getVisited().add(neighborId);
                    state.setQueueOrStack(new ArrayList<>(queue));

                    for (GraphVertex v : graph.getVertices()) {
                        if (v.getId().equals(neighborId)) v.setState("queued");
                    }
                    for (GraphEdge e : graph.getEdges()) {
                        if ((e.getSource().equals(curr) && e.getTarget().equals(neighborId)) ||
                            (!graph.isDirected() && e.getSource().equals(neighborId) && e.getTarget().equals(curr))) {
                            e.setState("active");
                        }
                    }

                    steps.add(new ExecutionStep(
                            steps.size(), "enqueue",
                            String.format("Discovered neighbor %s along edge (%s->%s). Enqueued.", neighborId, curr, neighborId),
                            5, state.copy(),
                            Map.of("visited", visitedSet.size(), "queueSize", queue.size()), null
                    ));
                }
            }

            for (GraphVertex v : graph.getVertices()) {
                if (v.getId().equals(curr)) v.setState("visited");
            }
        }

        state.setCurrentVertex(null);
        state.setMessage(String.format("BFS complete! Traversal order: %s.", state.getTraversalOrder()));
        steps.add(new ExecutionStep(
                steps.size(), "complete", state.getMessage(), 7, state.copy(),
                Map.of("visited", visitedSet.size(), "queueSize", 0), null
        ));

        return steps;
    }

    // ==========================================
    // 2. DFS
    // ==========================================
    public List<ExecutionStep> dfs(GraphData initialGraph, String startVertexId) {
        List<ExecutionStep> steps = new ArrayList<>();
        GraphData graph = initialGraph != null ? initialGraph.copy() : getSampleGraph();
        String start = startVertexId != null ? startVertexId : graph.getVertices().get(0).getId();

        GraphTraversalState state = new GraphTraversalState();
        state.setGraph(graph);
        state.setStartVertex(start);
        state.setQueueOrStack(new ArrayList<>(List.of(start)));
        state.setVisited(new ArrayList<>());
        state.setMessage(String.format("DFS initialized starting at vertex %s.", start));

        steps.add(new ExecutionStep(
                steps.size(), "init", state.getMessage(), 1, state.copy(),
                Map.of("visited", 0, "stackDepth", 1), null
        ));

        Map<String, List<GraphEdge>> adj = getAdjacencyList(graph);
        Set<String> visitedSet = new HashSet<>();
        List<String> callStack = new ArrayList<>();

        dfsRecursive(start, adj, visitedSet, callStack, graph, state, steps);

        state.setCurrentVertex(null);
        state.setQueueOrStack(Collections.emptyList());
        state.setMessage(String.format("DFS complete! Visited order: %s.", state.getTraversalOrder()));
        steps.add(new ExecutionStep(
                steps.size(), "complete", state.getMessage(), 7, state.copy(),
                Map.of("visited", visitedSet.size(), "stackDepth", 0), null
        ));

        return steps;
    }

    private void dfsRecursive(String curr, Map<String, List<GraphEdge>> adj, Set<String> visitedSet,
                              List<String> callStack, GraphData graph, GraphTraversalState state, List<ExecutionStep> steps) {
        visitedSet.add(curr);
        callStack.add(curr);
        state.getVisited().add(curr);
        state.getTraversalOrder().add(curr);
        state.setCurrentVertex(curr);
        state.setQueueOrStack(new ArrayList<>(callStack));

        for (GraphVertex v : graph.getVertices()) {
            if (v.getId().equals(curr)) v.setState("current");
        }

        steps.add(new ExecutionStep(
                steps.size(), "visit",
                String.format("Visiting vertex %s. Pushed onto recursion stack [%s].", curr, String.join(" -> ", callStack)),
                2, state.copy(),
                Map.of("visited", visitedSet.size(), "stackDepth", callStack.size()), null
        ));

        List<GraphEdge> neighbors = adj.getOrDefault(curr, Collections.emptyList());
        for (GraphEdge edge : neighbors) {
            String neighborId = edge.getTarget();
            if (!visitedSet.contains(neighborId)) {
                for (GraphEdge e : graph.getEdges()) {
                    if ((e.getSource().equals(curr) && e.getTarget().equals(neighborId)) ||
                        (!graph.isDirected() && e.getSource().equals(neighborId) && e.getTarget().equals(curr))) {
                        e.setState("active");
                    }
                }
                dfsRecursive(neighborId, adj, visitedSet, callStack, graph, state, steps);
            }
        }

        for (GraphVertex v : graph.getVertices()) {
            if (v.getId().equals(curr)) v.setState("visited");
        }
        callStack.remove(callStack.size() - 1);
        state.setQueueOrStack(new ArrayList<>(callStack));

        steps.add(new ExecutionStep(
                steps.size(), "backtrack",
                String.format("Unwound vertex %s from recursion stack. Backtracking.", curr),
                5, state.copy(),
                Map.of("visited", visitedSet.size(), "stackDepth", callStack.size()), null
        ));
    }

    // ==========================================
    // 3. DIJKSTRA
    // ==========================================
    public List<ExecutionStep> dijkstra(GraphData initialGraph, String startVertexId) {
        List<ExecutionStep> steps = new ArrayList<>();
        GraphData graph = initialGraph != null ? initialGraph.copy() : getSampleGraph();
        String start = startVertexId != null ? startVertexId : graph.getVertices().get(0).getId();

        ShortestPathState state = new ShortestPathState();
        state.setAlgorithm("dijkstra");
        state.setGraph(graph);
        state.setStartVertex(start);

        Map<String, Object> dist = new HashMap<>();
        Map<String, String> pred = new HashMap<>();
        for (GraphVertex v : graph.getVertices()) {
            dist.put(v.getId(), v.getId().equals(start) ? 0.0 : Double.POSITIVE_INFINITY);
            pred.put(v.getId(), null);
        }
        state.setDistances(dist);
        state.setPredecessors(pred);
        state.setMessage(String.format("Dijkstra initialized at source vertex %s. dist[%s] = 0.", start, start));

        steps.add(new ExecutionStep(
                steps.size(), "init", state.getMessage(), 1, state.copy(),
                Map.of("settledCount", 0), null
        ));

        Map<String, List<GraphEdge>> adj = getAdjacencyList(graph);
        Set<String> settled = new HashSet<>();

        while (settled.size() < graph.getVertices().size()) {
            // Find unsettled node with minimum distance
            String u = null;
            double minD = Double.POSITIVE_INFINITY;
            for (GraphVertex v : graph.getVertices()) {
                String vid = v.getId();
                if (!settled.contains(vid)) {
                    double d = ((Number) dist.get(vid)).doubleValue();
                    if (d < minD) {
                        minD = d;
                        u = vid;
                    }
                }
            }

            if (u == null || Double.isInfinite(minD)) break; // remaining nodes unreachable

            settled.add(u);
            state.setCurrentVertex(u);
            state.getSettled().add(u);

            for (GraphVertex v : graph.getVertices()) {
                if (v.getId().equals(u)) v.setState("current");
            }

            steps.add(new ExecutionStep(
                    steps.size(), "settle",
                    String.format("Settled vertex %s with finalized shortest distance %s.", u, minD),
                    3, state.copy(),
                    Map.of("settledCount", settled.size()), null
            ));

            // Relax outgoing edges
            for (GraphEdge edge : adj.getOrDefault(u, Collections.emptyList())) {
                String v = edge.getTarget();
                if (!settled.contains(v)) {
                    double alt = minD + edge.getWeight();
                    double currD = ((Number) dist.get(v)).doubleValue();

                    if (alt < currD) {
                        dist.put(v, alt);
                        pred.put(v, u);

                        for (GraphEdge e : graph.getEdges()) {
                            if ((e.getSource().equals(u) && e.getTarget().equals(v)) ||
                                (!graph.isDirected() && e.getSource().equals(v) && e.getTarget().equals(u))) {
                                e.setState("relaxed");
                            }
                        }

                        steps.add(new ExecutionStep(
                                steps.size(), "relax",
                                String.format("Relaxed edge (%s->%s, weight %d). Updated dist[%s] from %s to %s.",
                                        u, v, edge.getWeight(), v, Double.isInfinite(currD) ? "∞" : currD, alt),
                                5, state.copy(),
                                Map.of("settledCount", settled.size()), null
                        ));
                    }
                }
            }

            for (GraphVertex v : graph.getVertices()) {
                if (v.getId().equals(u)) v.setState("visited");
            }
        }

        state.setCurrentVertex(null);
        state.setMessage("Dijkstra completed! All reachable shortest paths determined.");
        steps.add(new ExecutionStep(
                steps.size(), "complete", state.getMessage(), 7, state.copy(),
                Map.of("settledCount", settled.size()), null
        ));

        return steps;
    }

    // ==========================================
    // 4. BELLMAN-FORD
    // ==========================================
    public List<ExecutionStep> bellmanFord(GraphData initialGraph, String startVertexId) {
        List<ExecutionStep> steps = new ArrayList<>();
        GraphData graph = initialGraph != null ? initialGraph.copy() : getSampleGraph();
        String start = startVertexId != null ? startVertexId : graph.getVertices().get(0).getId();

        ShortestPathState state = new ShortestPathState();
        state.setAlgorithm("bellman-ford");
        state.setGraph(graph);
        state.setStartVertex(start);

        Map<String, Object> dist = new HashMap<>();
        Map<String, String> pred = new HashMap<>();
        for (GraphVertex v : graph.getVertices()) {
            dist.put(v.getId(), v.getId().equals(start) ? 0.0 : Double.POSITIVE_INFINITY);
            pred.put(v.getId(), null);
        }
        state.setDistances(dist);
        state.setPredecessors(pred);
        state.setMessage(String.format("Bellman-Ford initialized at source %s. Running |V| - 1 = %d relaxation iterations.",
                start, graph.getVertices().size() - 1));

        steps.add(new ExecutionStep(
                steps.size(), "init", state.getMessage(), 1, state.copy()
        ));

        int V = graph.getVertices().size();
        for (int i = 1; i <= V - 1; i++) {
            boolean relaxedAny = false;
            for (GraphEdge edge : graph.getEdges()) {
                String u = edge.getSource();
                String v = edge.getTarget();
                double du = ((Number) dist.get(u)).doubleValue();
                double dv = ((Number) dist.get(v)).doubleValue();

                if (!Double.isInfinite(du) && du + edge.getWeight() < dv) {
                    dist.put(v, du + edge.getWeight());
                    pred.put(v, u);
                    edge.setState("relaxed");
                    relaxedAny = true;

                    steps.add(new ExecutionStep(
                            steps.size(), "relax",
                            String.format("Pass %d/%d: Relaxed edge (%s->%s, weight %d). dist[%s] is now %s.",
                                    i, V - 1, u, v, edge.getWeight(), v, du + edge.getWeight()),
                            4, state.copy()
                    ));
                    edge.setState("default");
                }
            }
            if (!relaxedAny) {
                steps.add(new ExecutionStep(
                        steps.size(), "early-exit",
                        String.format("Pass %d: No edges relaxed. Distances have converged early.", i),
                        6, state.copy()
                ));
                break;
            }
        }

        // Negative cycle detection pass
        boolean hasNegativeCycle = false;
        for (GraphEdge edge : graph.getEdges()) {
            double du = ((Number) dist.get(edge.getSource())).doubleValue();
            double dv = ((Number) dist.get(edge.getTarget())).doubleValue();
            if (!Double.isInfinite(du) && du + edge.getWeight() < dv) {
                hasNegativeCycle = true;
                break;
            }
        }
        state.setHasNegativeWeightCycle(hasNegativeCycle);
        state.setMessage(hasNegativeCycle
                ? "NEGATIVE WEIGHT CYCLE DETECTED! Distances would decrease indefinitely."
                : "Bellman-Ford completed successfully. No negative cycles found.");

        steps.add(new ExecutionStep(
                steps.size(), hasNegativeCycle ? "negative-cycle" : "complete", state.getMessage(), 7, state.copy()
        ));

        return steps;
    }

    // ==========================================
    // 5. FLOYD-WARSHALL
    // ==========================================
    public List<ExecutionStep> floydWarshall(GraphData initialGraph) {
        List<ExecutionStep> steps = new ArrayList<>();
        GraphData graph = initialGraph != null ? initialGraph.copy() : getSampleGraph();

        ShortestPathState state = new ShortestPathState();
        state.setAlgorithm("floyd-warshall");
        state.setGraph(graph);

        List<String> labels = new ArrayList<>();
        for (GraphVertex v : graph.getVertices()) labels.add(v.getId());
        int V = labels.size();

        double[][] d = new double[V][V];
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                d[i][j] = (i == j) ? 0.0 : Double.POSITIVE_INFINITY;
            }
        }

        for (GraphEdge edge : graph.getEdges()) {
            int u = labels.indexOf(edge.getSource());
            int v = labels.indexOf(edge.getTarget());
            if (u >= 0 && v >= 0) {
                d[u][v] = Math.min(d[u][v], (double) edge.getWeight());
                if (!graph.isDirected()) {
                    d[v][u] = Math.min(d[v][u], (double) edge.getWeight());
                }
            }
        }

        state.setMatrix(createMatrixMap(labels, d));
        state.setMessage("Floyd-Warshall initialized: All-Pairs Shortest Path matrix constructed.");

        steps.add(new ExecutionStep(
                steps.size(), "init", state.getMessage(), 1, state.copy()
        ));

        for (int k = 0; k < V; k++) {
            String kLabel = labels.get(k);
            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    if (d[i][k] != Double.POSITIVE_INFINITY && d[k][j] != Double.POSITIVE_INFINITY) {
                        double alt = d[i][k] + d[k][j];
                        if (alt < d[i][j]) {
                            d[i][j] = alt;
                            state.setMatrix(createMatrixMap(labels, d));
                            steps.add(new ExecutionStep(
                                    steps.size(), "relax-pair",
                                    String.format("Intermediate node %s improved path from %s to %s: new dist = %s.",
                                            kLabel, labels.get(i), labels.get(j), alt),
                                    4, state.copy()
                            ));
                        }
                    }
                }
            }
        }

        state.setMessage("Floyd-Warshall completed! Computed all-pairs shortest paths in O(V³) time.");
        steps.add(new ExecutionStep(
                steps.size(), "complete", state.getMessage(), 6, state.copy()
        ));

        return steps;
    }

    private Map<String, Object> createMatrixMap(List<String> labels, double[][] d) {
        Map<String, Object> map = new HashMap<>();
        map.put("labels", labels);
        List<List<Object>> matrixList = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            List<Object> row = new ArrayList<>();
            for (int j = 0; j < labels.size(); j++) {
                row.add(Double.isInfinite(d[i][j]) ? "∞" : (int) d[i][j]);
            }
            matrixList.add(row);
        }
        map.put("dist", matrixList);
        return map;
    }

    // ==========================================
    // 6. KRUSKAL (MST with DSU)
    // ==========================================
    public List<ExecutionStep> kruskal(GraphData initialGraph) {
        List<ExecutionStep> steps = new ArrayList<>();
        GraphData graph = initialGraph != null ? initialGraph.copy() : getSampleGraph();

        MSTState state = new MSTState();
        state.setAlgorithm("kruskal");
        state.setGraph(graph);
        state.setMstEdges(new ArrayList<>());
        state.setTotalWeight(0);
        state.setComponentsCount(graph.getVertices().size());
        state.setMessage("Kruskal's MST algorithm initialized. Sorting edges by ascending weight.");

        steps.add(new ExecutionStep(
                steps.size(), "init", state.getMessage(), 1, state.copy()
        ));

        // Sort edges
        List<GraphEdge> sortedEdges = new ArrayList<>(graph.getEdges());
        sortedEdges.sort(Comparator.comparingInt(GraphEdge::getWeight));

        // DSU
        Map<String, String> parent = new HashMap<>();
        for (GraphVertex v : graph.getVertices()) parent.put(v.getId(), v.getId());

        int edgeIdx = 0;
        while (edgeIdx < sortedEdges.size() && state.getMstEdges().size() < graph.getVertices().size() - 1) {
            GraphEdge edge = sortedEdges.get(edgeIdx);
            state.setCurrentEdge(edge);
            edge.setState("active");

            steps.add(new ExecutionStep(
                    steps.size(), "examine-edge",
                    String.format("Considering smallest available edge (%s - %s, weight %d).",
                            edge.getSource(), edge.getTarget(), edge.getWeight()),
                    3, state.copy()
            ));

            String rootU = find(parent, edge.getSource());
            String rootV = find(parent, edge.getTarget());

            if (!rootU.equals(rootV)) {
                parent.put(rootU, rootV);
                edge.setState("mst");
                state.getMstEdges().add(edge);
                state.setTotalWeight(state.getTotalWeight() + edge.getWeight());
                state.setComponentsCount(state.getComponentsCount() - 1);

                steps.add(new ExecutionStep(
                        steps.size(), "add-edge",
                        String.format("Added edge (%s - %s, weight %d) to MST. Connects separate components.",
                                edge.getSource(), edge.getTarget(), edge.getWeight()),
                        5, state.copy()
                ));
            } else {
                edge.setState("rejected");
                state.getRejectedEdges().add(edge);

                steps.add(new ExecutionStep(
                        steps.size(), "reject-cycle",
                        String.format("Rejected edge (%s - %s, weight %d): would form a cycle!",
                                edge.getSource(), edge.getTarget(), edge.getWeight()),
                        7, state.copy()
                ));
            }

            edgeIdx++;
        }

        state.setCurrentEdge(null);
        state.setMessage(String.format("Kruskal's MST complete! Total edges: %d, Total Weight: %d.",
                state.getMstEdges().size(), state.getTotalWeight()));
        steps.add(new ExecutionStep(
                steps.size(), "complete", state.getMessage(), 8, state.copy()
        ));

        return steps;
    }

    private String find(Map<String, String> parent, String x) {
        if (!parent.get(x).equals(x)) {
            parent.put(x, find(parent, parent.get(x)));
        }
        return parent.get(x);
    }

    // ==========================================
    // 7. PRIM'S MST
    // ==========================================
    public List<ExecutionStep> prim(GraphData initialGraph, String startVertexId) {
        List<ExecutionStep> steps = new ArrayList<>();
        GraphData graph = initialGraph != null ? initialGraph.copy() : getSampleGraph();
        String start = startVertexId != null ? startVertexId : graph.getVertices().get(0).getId();

        MSTState state = new MSTState();
        state.setAlgorithm("prim");
        state.setGraph(graph);
        state.setMstEdges(new ArrayList<>());
        state.setTotalWeight(0);
        state.setMessage(String.format("Prim's MST initialized at vertex %s.", start));

        steps.add(new ExecutionStep(
                steps.size(), "init", state.getMessage(), 1, state.copy()
        ));

        Set<String> inMST = new HashSet<>();
        inMST.add(start);

        for (GraphVertex v : graph.getVertices()) {
            if (v.getId().equals(start)) v.setState("current");
        }

        while (inMST.size() < graph.getVertices().size()) {
            GraphEdge minEdge = null;
            int minWeight = Integer.MAX_VALUE;

            for (GraphEdge edge : graph.getEdges()) {
                boolean uIn = inMST.contains(edge.getSource());
                boolean vIn = inMST.contains(edge.getTarget());

                if ((uIn && !vIn) || (!uIn && vIn)) {
                    if (edge.getWeight() < minWeight) {
                        minWeight = edge.getWeight();
                        minEdge = edge;
                    }
                }
            }

            if (minEdge == null) break; // Disconnected graph

            String newVertex = inMST.contains(minEdge.getSource()) ? minEdge.getTarget() : minEdge.getSource();
            inMST.add(newVertex);
            minEdge.setState("mst");
            state.getMstEdges().add(minEdge);
            state.setTotalWeight(state.getTotalWeight() + minEdge.getWeight());

            for (GraphVertex v : graph.getVertices()) {
                if (v.getId().equals(newVertex)) v.setState("visited");
            }

            steps.add(new ExecutionStep(
                    steps.size(), "add-edge",
                    String.format("Added cheapest crossing edge (%s - %s, weight %d) connecting %s to MST.",
                            minEdge.getSource(), minEdge.getTarget(), minEdge.getWeight(), newVertex),
                    4, state.copy()
            ));
        }

        state.setMessage(String.format("Prim's MST complete! Total Weight: %d across %d edges.",
                state.getTotalWeight(), state.getMstEdges().size()));
        steps.add(new ExecutionStep(
                steps.size(), "complete", state.getMessage(), 6, state.copy()
        ));

        return steps;
    }
}
