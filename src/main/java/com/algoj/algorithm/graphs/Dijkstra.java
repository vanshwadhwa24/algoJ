package com.algoj.algorithm.graphs;

import com.algoj.algorithm.GraphAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.GraphModel.*;
import java.util.*;

public class Dijkstra extends GraphAlgorithm {

    public Dijkstra() {
        super("dijkstra");
    }

    @Override
    protected List<ExecutionStep> trace(GraphData initialGraph, String startVertexId) {
        List<ExecutionStep> steps = new ArrayList<>();
        GraphData graph = initialGraph != null ? initialGraph.copy() : sampleGraph();
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
}
