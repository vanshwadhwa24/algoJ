import { ExecutionStep } from '../../types/execution';
import { GraphData, cloneGraph, getAdjacencyList } from '../structures/graph';

export interface GraphTraversalState {
  graph: GraphData;
  startVertex: string;
  queueOrStack: string[];
  visited: string[];
  traversalOrder: string[];
  currentVertex: string | null;
  message: string;
}

export function generateBFSSteps(
  initialGraph: GraphData,
  startVertexId?: string
): ExecutionStep<GraphTraversalState>[] {
  const steps: ExecutionStep<GraphTraversalState>[] = [];
  const graph = cloneGraph(initialGraph);
  const start = startVertexId || graph.startVertexId || graph.vertices[0]?.id || 'A';

  const state: GraphTraversalState = {
    graph,
    startVertex: start,
    queueOrStack: [start],
    visited: [start],
    traversalOrder: [],
    currentVertex: null,
    message: `Initialized BFS starting at vertex ${start}.`,
  };

  // Mark start vertex as queued
  const startV = state.graph.vertices.find((v) => v.id === start);
  if (startV) startV.state = 'queued';

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `Added start vertex ${start} to queue and marked as visited. Queue: [${start}].`,
    codeLine: 2,
    state: JSON.parse(JSON.stringify(state)),
    counters: { visited: 1, queueSize: 1 },
  });

  const adj = getAdjacencyList(graph);

  while (state.queueOrStack.length > 0) {
    const current = state.queueOrStack.shift()!;
    state.currentVertex = current;
    state.traversalOrder.push(current);

    const currV = state.graph.vertices.find((v) => v.id === current);
    if (currV) currV.state = 'current';

    steps.push({
      stepIndex: steps.length,
      operationType: 'dequeue',
      description: `Dequeued vertex ${current}. Added to traversal order: [${state.traversalOrder.join(', ')}].`,
      codeLine: 5,
      state: JSON.parse(JSON.stringify(state)),
      counters: { visited: state.visited.length, queueSize: state.queueOrStack.length },
    });

    const neighbors = adj[current] || [];
    for (const neighbor of neighbors) {
      const neighborId = neighbor.target;
      const edge = state.graph.edges.find(
        (e) =>
          (e.source === current && e.target === neighborId) ||
          (!state.graph.isDirected && e.source === neighborId && e.target === current)
      );

      if (!state.visited.includes(neighborId)) {
        state.visited.push(neighborId);
        state.queueOrStack.push(neighborId);
        const neighborV = state.graph.vertices.find((v) => v.id === neighborId);
        if (neighborV) neighborV.state = 'queued';
        if (edge) edge.state = 'traversed';

        steps.push({
          stepIndex: steps.length,
          operationType: 'discover-neighbor',
          description: `Discovered unvisited neighbor ${neighborId}. Marked visited and enqueued. Queue: [${state.queueOrStack.join(', ')}].`,
          codeLine: 8,
          state: JSON.parse(JSON.stringify(state)),
          counters: { visited: state.visited.length, queueSize: state.queueOrStack.length },
        });
      }
    }

    if (currV) currV.state = 'visited';
  }

  // Check disconnected components
  const unvisitedVertices = state.graph.vertices.filter((v) => !state.visited.includes(v.id));
  const isDisconnected = unvisitedVertices.length > 0;

  steps.push({
    stepIndex: steps.length,
    operationType: 'complete',
    description: isDisconnected
      ? `BFS traversal from ${start} finished. ${unvisitedVertices.length} vertex/vertices ([${unvisitedVertices.map((v) => v.id).join(', ')}]) are unreachable from ${start}.`
      : `BFS completed! Visited all ${state.visited.length} vertices in order: [${state.traversalOrder.join(', ')}].`,
    codeLine: 12,
    state: JSON.parse(JSON.stringify(state)),
    counters: { visited: state.visited.length, queueSize: 0 },
  });

  return steps;
}

export function generateDFSSteps(
  initialGraph: GraphData,
  startVertexId?: string
): ExecutionStep<GraphTraversalState>[] {
  const steps: ExecutionStep<GraphTraversalState>[] = [];
  const graph = cloneGraph(initialGraph);
  const start = startVertexId || graph.startVertexId || graph.vertices[0]?.id || 'A';

  const state: GraphTraversalState = {
    graph,
    startVertex: start,
    queueOrStack: [start],
    visited: [],
    traversalOrder: [],
    currentVertex: null,
    message: `Initialized DFS starting at vertex ${start}.`,
  };

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `Pushed start vertex ${start} onto recursion call stack.`,
    codeLine: 1,
    state: JSON.parse(JSON.stringify(state)),
    counters: { visited: 0, stackDepth: 1 },
  });

  const adj = getAdjacencyList(graph);

  function dfsRec(u: string) {
    state.visited.push(u);
    state.traversalOrder.push(u);
    state.currentVertex = u;

    const uNode = state.graph.vertices.find((v) => v.id === u);
    if (uNode) uNode.state = 'current';

    steps.push({
      stepIndex: steps.length,
      operationType: 'visit',
      description: `Visited vertex ${u}. Added to DFS order: [${state.traversalOrder.join(', ')}].`,
      codeLine: 3,
      state: JSON.parse(JSON.stringify(state)),
      counters: { visited: state.visited.length, stackDepth: state.queueOrStack.length },
    });

    const neighbors = adj[u] || [];
    for (const neighbor of neighbors) {
      const v = neighbor.target;
      const edge = state.graph.edges.find(
        (e) =>
          (e.source === u && e.target === v) ||
          (!state.graph.isDirected && e.source === v && e.target === u)
      );

      if (!state.visited.includes(v)) {
        if (edge) edge.state = 'traversed';
        state.queueOrStack.push(v);

        steps.push({
          stepIndex: steps.length,
          operationType: 'recurse',
          description: `Exploring edge (${u} -> ${v}). Calling DFS(${v}). Call stack: [${state.queueOrStack.join(' -> ')}].`,
          codeLine: 6,
          state: JSON.parse(JSON.stringify(state)),
          counters: { visited: state.visited.length, stackDepth: state.queueOrStack.length },
        });

        dfsRec(v);

        state.queueOrStack.pop();
        steps.push({
          stepIndex: steps.length,
          operationType: 'backtrack',
          description: `Backtracking from ${v} to ${u}. Call stack returned to depth ${state.queueOrStack.length}.`,
          codeLine: 8,
          state: JSON.parse(JSON.stringify(state)),
          counters: { visited: state.visited.length, stackDepth: state.queueOrStack.length },
        });
      }
    }

    if (uNode) uNode.state = 'visited';
  }

  dfsRec(start);

  const unvisitedVertices = state.graph.vertices.filter((v) => !state.visited.includes(v.id));
  steps.push({
    stepIndex: steps.length,
    operationType: 'complete',
    description:
      unvisitedVertices.length > 0
        ? `DFS from ${start} finished. Reachable component explored. Unreachable: [${unvisitedVertices.map((v) => v.id).join(', ')}].`
        : `DFS complete! Visited all ${state.visited.length} vertices in depth-first order: [${state.traversalOrder.join(', ')}].`,
    codeLine: 10,
    state: JSON.parse(JSON.stringify(state)),
    counters: { visited: state.visited.length, stackDepth: 0 },
  });

  return steps;
}
