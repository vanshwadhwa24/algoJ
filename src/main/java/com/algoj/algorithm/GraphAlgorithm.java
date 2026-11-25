package com.algoj.algorithm;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.GraphModel.*;
import java.util.*;

/** Template for graph algorithms: resolves the input graph (or the sample graph) and the start vertex. */
public abstract class GraphAlgorithm extends BaseAlgorithm {

    protected GraphAlgorithm(String id) {
        super(id);
    }

    @Override
    public List<ExecutionStep> run(AlgorithmInput in) {
        GraphData graph = in.graph() != null ? in.graph() : sampleGraph();
        String start = in.target() != null ? String.valueOf(in.target()) : null;
        return trace(graph, start);
    }

    protected abstract List<ExecutionStep> trace(GraphData graph, String startVertexId);

    public static GraphData sampleGraph() {
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

    protected static Map<String, List<GraphEdge>> getAdjacencyList(GraphData graph) {
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
}
