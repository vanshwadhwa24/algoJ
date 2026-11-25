package com.algoj.algorithm.graphs;

import com.algoj.algorithm.GraphAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.GraphModel.*;
import java.util.*;

public class Dfs extends GraphAlgorithm {

    public Dfs() {
        super("dfs");
    }

    @Override
    protected List<ExecutionStep> trace(GraphData initialGraph, String startVertexId) {
        List<ExecutionStep> steps = new ArrayList<>();
        GraphData graph = initialGraph != null ? initialGraph.copy() : sampleGraph();
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
}
