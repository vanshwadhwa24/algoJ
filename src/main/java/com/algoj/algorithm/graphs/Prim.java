package com.algoj.algorithm.graphs;

import com.algoj.algorithm.GraphAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.GraphModel.*;
import java.util.*;

public class Prim extends GraphAlgorithm {

    public Prim() {
        super("prim");
    }

    @Override
    protected List<ExecutionStep> trace(GraphData initialGraph, String startVertexId) {
        List<ExecutionStep> steps = new ArrayList<>();
        GraphData graph = initialGraph != null ? initialGraph.copy() : sampleGraph();
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
