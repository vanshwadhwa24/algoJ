package com.algoj.algorithm.graphs;

import com.algoj.algorithm.GraphAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.GraphModel.*;
import java.util.*;

public class BellmanFord extends GraphAlgorithm {

    public BellmanFord() {
        super("bellman-ford");
    }

    @Override
    protected List<ExecutionStep> trace(GraphData initialGraph, String startVertexId) {
        List<ExecutionStep> steps = new ArrayList<>();
        GraphData graph = initialGraph != null ? initialGraph.copy() : sampleGraph();
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
        List<Arc> arcs = arcsOf(graph);
        for (int i = 1; i <= V - 1; i++) {
            boolean relaxedAny = false;
            for (Arc arc : arcs) {
                GraphEdge edge = arc.edge();
                String u = arc.from();
                String v = arc.to();
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
        for (Arc arc : arcs) {
            double du = ((Number) dist.get(arc.from())).doubleValue();
            double dv = ((Number) dist.get(arc.to())).doubleValue();
            if (!Double.isInfinite(du) && du + arc.edge().getWeight() < dv) {
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

    /** One direction of travel along an edge. Undirected edges contribute both directions. */
    private record Arc(GraphEdge edge, String from, String to) {}

    private static List<Arc> arcsOf(GraphData graph) {
        List<Arc> arcs = new ArrayList<>();
        for (GraphEdge e : graph.getEdges()) {
            arcs.add(new Arc(e, e.getSource(), e.getTarget()));
            if (!graph.isDirected() && !e.isDirected()) {
                arcs.add(new Arc(e, e.getTarget(), e.getSource()));
            }
        }
        return arcs;
    }
}
