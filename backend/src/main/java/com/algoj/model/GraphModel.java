package com.algoj.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GraphModel {

    public static class GraphVertex {
        private String id;
        private String label;
        private double x;
        private double y;
        private String state; // default, active, visited, queued, current

        public GraphVertex() {}
        public GraphVertex(String id, String label, double x, double y) {
            this.id = id;
            this.label = label;
            this.x = x;
            this.y = y;
            this.state = "default";
        }
        public GraphVertex copy() {
            GraphVertex v = new GraphVertex(id, label, x, y);
            v.state = this.state;
            return v;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public double getX() { return x; }
        public void setX(double x) { this.x = x; }
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
    }

    public static class GraphEdge {
        private String id;
        private String source;
        private String target;
        private int weight;
        private boolean directed;
        private String state; // default, active, traversed, mst, relaxed, rejected

        public GraphEdge() {}
        public GraphEdge(String id, String source, String target, int weight, boolean directed) {
            this.id = id;
            this.source = source;
            this.target = target;
            this.weight = weight;
            this.directed = directed;
            this.state = "default";
        }
        public GraphEdge copy() {
            GraphEdge e = new GraphEdge(id, source, target, weight, directed);
            e.state = this.state;
            return e;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
        public int getWeight() { return weight; }
        public void setWeight(int weight) { this.weight = weight; }
        public boolean isDirected() { return directed; }
        public void setDirected(boolean directed) { this.directed = directed; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
    }

    public static class GraphData {
        private List<GraphVertex> vertices = new ArrayList<>();
        private List<GraphEdge> edges = new ArrayList<>();
        private boolean isDirected;
        private boolean isWeighted;
        private String startVertexId;

        public GraphData() {}

        public GraphData copy() {
            GraphData g = new GraphData();
            g.isDirected = this.isDirected;
            g.isWeighted = this.isWeighted;
            g.startVertexId = this.startVertexId;
            for (GraphVertex v : this.vertices) g.vertices.add(v.copy());
            for (GraphEdge e : this.edges) g.edges.add(e.copy());
            return g;
        }

        public List<GraphVertex> getVertices() { return vertices; }
        public void setVertices(List<GraphVertex> vertices) { this.vertices = vertices; }
        public List<GraphEdge> getEdges() { return edges; }
        public void setEdges(List<GraphEdge> edges) { this.edges = edges; }
        public boolean isDirected() { return isDirected; }
        public void setDirected(boolean directed) { isDirected = directed; }
        public boolean isWeighted() { return isWeighted; }
        public void setWeighted(boolean weighted) { isWeighted = weighted; }
        public String getStartVertexId() { return startVertexId; }
        public void setStartVertexId(String startVertexId) { this.startVertexId = startVertexId; }
    }

    public static class GraphTraversalState {
        private GraphData graph;
        private String startVertex;
        private List<String> queueOrStack = new ArrayList<>();
        private List<String> visited = new ArrayList<>();
        private List<String> traversalOrder = new ArrayList<>();
        private String currentVertex;
        private String message;

        public GraphTraversalState() {}
        public GraphTraversalState copy() {
            GraphTraversalState s = new GraphTraversalState();
            s.graph = this.graph != null ? this.graph.copy() : null;
            s.startVertex = this.startVertex;
            s.queueOrStack = new ArrayList<>(this.queueOrStack);
            s.visited = new ArrayList<>(this.visited);
            s.traversalOrder = new ArrayList<>(this.traversalOrder);
            s.currentVertex = this.currentVertex;
            s.message = this.message;
            return s;
        }

        public GraphData getGraph() { return graph; }
        public void setGraph(GraphData graph) { this.graph = graph; }
        public String getStartVertex() { return startVertex; }
        public void setStartVertex(String startVertex) { this.startVertex = startVertex; }
        public List<String> getQueueOrStack() { return queueOrStack; }
        public void setQueueOrStack(List<String> queueOrStack) { this.queueOrStack = queueOrStack; }
        public List<String> getVisited() { return visited; }
        public void setVisited(List<String> visited) { this.visited = visited; }
        public List<String> getTraversalOrder() { return traversalOrder; }
        public void setTraversalOrder(List<String> traversalOrder) { this.traversalOrder = traversalOrder; }
        public String getCurrentVertex() { return currentVertex; }
        public void setCurrentVertex(String currentVertex) { this.currentVertex = currentVertex; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class ShortestPathState {
        private String algorithm;
        private GraphData graph;
        private String startVertex;
        private Map<String, Object> distances = new HashMap<>();
        private Map<String, String> predecessors = new HashMap<>();
        private Map<String, Object> matrix;
        private String currentVertex;
        private Map<String, Object> activeEdge;
        private List<String> settled = new ArrayList<>();
        private boolean hasNegativeWeightCycle;
        private String message;

        public ShortestPathState() {}
        public ShortestPathState copy() {
            ShortestPathState s = new ShortestPathState();
            s.algorithm = this.algorithm;
            s.graph = this.graph != null ? this.graph.copy() : null;
            s.startVertex = this.startVertex;
            s.distances = new HashMap<>(this.distances);
            s.predecessors = new HashMap<>(this.predecessors);
            s.matrix = this.matrix != null ? new HashMap<>(this.matrix) : null;
            s.currentVertex = this.currentVertex;
            s.activeEdge = this.activeEdge != null ? new HashMap<>(this.activeEdge) : null;
            s.settled = new ArrayList<>(this.settled);
            s.hasNegativeWeightCycle = this.hasNegativeWeightCycle;
            s.message = this.message;
            return s;
        }

        public String getAlgorithm() { return algorithm; }
        public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
        public GraphData getGraph() { return graph; }
        public void setGraph(GraphData graph) { this.graph = graph; }
        public String getStartVertex() { return startVertex; }
        public void setStartVertex(String startVertex) { this.startVertex = startVertex; }
        public Map<String, Object> getDistances() { return distances; }
        public void setDistances(Map<String, Object> distances) { this.distances = distances; }
        public Map<String, String> getPredecessors() { return predecessors; }
        public void setPredecessors(Map<String, String> predecessors) { this.predecessors = predecessors; }
        public Map<String, Object> getMatrix() { return matrix; }
        public void setMatrix(Map<String, Object> matrix) { this.matrix = matrix; }
        public String getCurrentVertex() { return currentVertex; }
        public void setCurrentVertex(String currentVertex) { this.currentVertex = currentVertex; }
        public Map<String, Object> getActiveEdge() { return activeEdge; }
        public void setActiveEdge(Map<String, Object> activeEdge) { this.activeEdge = activeEdge; }
        public List<String> getSettled() { return settled; }
        public void setSettled(List<String> settled) { this.settled = settled; }
        public boolean isHasNegativeWeightCycle() { return hasNegativeWeightCycle; }
        public void setHasNegativeWeightCycle(boolean hasNegativeWeightCycle) { this.hasNegativeWeightCycle = hasNegativeWeightCycle; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class MSTState {
        private String algorithm;
        private GraphData graph;
        private List<GraphEdge> mstEdges = new ArrayList<>();
        private int totalWeight;
        private GraphEdge currentEdge;
        private List<GraphEdge> rejectedEdges = new ArrayList<>();
        private boolean isSpanningForest;
        private int componentsCount;
        private String message;

        public MSTState() {}
        public MSTState copy() {
            MSTState s = new MSTState();
            s.algorithm = this.algorithm;
            s.graph = this.graph != null ? this.graph.copy() : null;
            for (GraphEdge e : this.mstEdges) s.mstEdges.add(e.copy());
            s.totalWeight = this.totalWeight;
            s.currentEdge = this.currentEdge != null ? this.currentEdge.copy() : null;
            for (GraphEdge e : this.rejectedEdges) s.rejectedEdges.add(e.copy());
            s.isSpanningForest = this.isSpanningForest;
            s.componentsCount = this.componentsCount;
            s.message = this.message;
            return s;
        }

        public String getAlgorithm() { return algorithm; }
        public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
        public GraphData getGraph() { return graph; }
        public void setGraph(GraphData graph) { this.graph = graph; }
        public List<GraphEdge> getMstEdges() { return mstEdges; }
        public void setMstEdges(List<GraphEdge> mstEdges) { this.mstEdges = mstEdges; }
        public int getTotalWeight() { return totalWeight; }
        public void setTotalWeight(int totalWeight) { this.totalWeight = totalWeight; }
        public GraphEdge getCurrentEdge() { return currentEdge; }
        public void setCurrentEdge(GraphEdge currentEdge) { this.currentEdge = currentEdge; }
        public List<GraphEdge> getRejectedEdges() { return rejectedEdges; }
        public void setRejectedEdges(List<GraphEdge> rejectedEdges) { this.rejectedEdges = rejectedEdges; }
        public boolean isSpanningForest() { return isSpanningForest; }
        public void setSpanningForest(boolean spanningForest) { isSpanningForest = spanningForest; }
        public int getComponentsCount() { return componentsCount; }
        public void setComponentsCount(int componentsCount) { this.componentsCount = componentsCount; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
