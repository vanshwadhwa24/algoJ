package com.algoj.algorithm.graphs;

import com.algoj.algorithm.GraphAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.GraphModel.*;
import java.util.*;

public class Bfs extends GraphAlgorithm {

    public Bfs() {
        super("bfs");
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
}
