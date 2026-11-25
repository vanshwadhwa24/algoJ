package com.algoj.algorithm.graphs;

import com.algoj.algorithm.GraphAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.GraphModel.*;
import java.util.*;

public class Kruskal extends GraphAlgorithm {

    public Kruskal() {
        super("kruskal");
    }

    @Override
    protected List<ExecutionStep> trace(GraphData initialGraph, String startVertexId) {
        List<ExecutionStep> steps = new ArrayList<>();
        GraphData graph = initialGraph != null ? initialGraph.copy() : sampleGraph();

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
}
