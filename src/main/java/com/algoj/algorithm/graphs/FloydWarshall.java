package com.algoj.algorithm.graphs;

import com.algoj.algorithm.GraphAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.GraphModel.*;
import java.util.*;

public class FloydWarshall extends GraphAlgorithm {

    public FloydWarshall() {
        super("floyd-warshall");
    }

    @Override
    protected List<ExecutionStep> trace(GraphData initialGraph, String startVertexId) {
        List<ExecutionStep> steps = new ArrayList<>();
        GraphData graph = initialGraph != null ? initialGraph.copy() : sampleGraph();

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
}
