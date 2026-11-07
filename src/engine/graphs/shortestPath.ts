import { ExecutionStep } from '../../types/execution';
import { GraphData, cloneGraph, getAdjacencyList } from '../structures/graph';

export interface ShortestPathState {
  algorithm: 'dijkstra' | 'bellman-ford' | 'floyd-warshall';
  graph: GraphData;
  startVertex: string;
  distances: Record<string, number>;
  predecessors: Record<string, string | null>;
  matrix?: { labels: string[]; dist: (number | string)[][] };
  currentVertex: string | null;
  activeEdge?: { source: string; target: string; weight: number };
  settled: string[];
  hasNegativeWeightCycle: boolean;
  message: string;
}

// 1. DIJKSTRA'S ALGORITHM
export function generateDijkstraSteps(
  initialGraph: GraphData,
  startVertexId?: string
): ExecutionStep<ShortestPathState>[] {
  const steps: ExecutionStep<ShortestPathState>[] = [];
  const graph = cloneGraph(initialGraph);
  const start = startVertexId || graph.startVertexId || graph.vertices[0]?.id || 'A';

  // Check for negative weights
  const hasNegativeEdge = graph.edges.some((e) => e.weight < 0);

  const distances: Record<string, number> = {};
  const predecessors: Record<string, string | null> = {};
  graph.vertices.forEach((v) => {
    distances[v.id] = v.id === start ? 0 : Infinity;
    predecessors[v.id] = null;
  });

  const state: ShortestPathState = {
    algorithm: 'dijkstra',
    graph,
    startVertex: start,
    distances: { ...distances },
    predecessors: { ...predecessors },
    currentVertex: null,
    settled: [],
    hasNegativeWeightCycle: false,
    message: hasNegativeEdge
      ? 'WARNING: Graph contains negative weights. Dijkstra produces undefined results with negative weights!'
      : `Dijkstra initialized with source = ${start}.`,
  };

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: hasNegativeEdge
      ? 'WARNING: Graph has negative weight edges! Dijkstra assumes greedy monotonic distances and may fail. Bellman-Ford should be used instead.'
      : `Initialized distances: dist[${start}] = 0, all other vertices = ∞.`,
    codeLine: 2,
    state: JSON.parse(JSON.stringify(state)),
    counters: { settledCount: 0 },
  });

  const unvisited = new Set(graph.vertices.map((v) => v.id));
  const adj = getAdjacencyList(graph);

  while (unvisited.size > 0) {
    // Find unvisited vertex with min distance
    let minNode: string | null = null;
    let minDistance = Infinity;

    unvisited.forEach((nodeId) => {
      if (state.distances[nodeId] < minDistance) {
        minDistance = state.distances[nodeId];
        minNode = nodeId;
      }
    });

    if (!minNode || minDistance === Infinity) {
      // Remaining vertices are unreachable
      break;
    }

    unvisited.delete(minNode);
    state.settled.push(minNode);
    state.currentVertex = minNode;

    const uNode = state.graph.vertices.find((v) => v.id === minNode);
    if (uNode) uNode.state = 'current';

    steps.push({
      stepIndex: steps.length,
      operationType: 'select-min',
      description: `Extracted vertex ${minNode} with current minimum tentative distance ${minDistance}. Marked settled.`,
      codeLine: 5,
      state: JSON.parse(JSON.stringify(state)),
      counters: { settledCount: state.settled.length },
    });

    const neighbors = adj[minNode] || [];
    for (const neighbor of neighbors) {
      const v = neighbor.target;
      const weight = neighbor.weight;

      if (unvisited.has(v)) {
        state.activeEdge = { source: minNode, target: v, weight };
        const newDist = state.distances[minNode] + weight;

        const edgeInGraph = state.graph.edges.find(
          (e) =>
            (e.source === minNode && e.target === v) ||
            (!state.graph.isDirected && e.source === v && e.target === minNode)
        );
        if (edgeInGraph) edgeInGraph.state = 'active';

        steps.push({
          stepIndex: steps.length,
          operationType: 'inspect-edge',
          description: `Evaluating edge (${minNode} -> ${v}, w=${weight}): candidate distance = ${state.distances[minNode]} + ${weight} = ${newDist}. (Current dist[${v}] = ${state.distances[v] === Infinity ? '∞' : state.distances[v]}).`,
          codeLine: 8,
          state: JSON.parse(JSON.stringify(state)),
          counters: { settledCount: state.settled.length },
        });

        if (newDist < state.distances[v]) {
          state.distances[v] = newDist;
          state.predecessors[v] = minNode;
          if (edgeInGraph) edgeInGraph.state = 'relaxed';

          steps.push({
            stepIndex: steps.length,
            operationType: 'relax',
            description: `Relaxed edge! Updated dist[${v}] = ${newDist}, predecessor[${v}] = ${minNode}.`,
            codeLine: 10,
            state: JSON.parse(JSON.stringify(state)),
            counters: { settledCount: state.settled.length },
          });
        }
      }
    }

    if (uNode) uNode.state = 'visited';
    state.activeEdge = undefined;
  }

  steps.push({
    stepIndex: steps.length,
    operationType: 'complete',
    description: `Dijkstra completed. Shortest path distances from ${start}: ${Object.entries(state.distances).map(([k, d]) => `${k}:${d === Infinity ? '∞' : d}`).join(', ')}.`,
    codeLine: 14,
    state: JSON.parse(JSON.stringify(state)),
    counters: { settledCount: state.settled.length },
  });

  return steps;
}

// 2. BELLMAN-FORD ALGORITHM
export function generateBellmanFordSteps(
  initialGraph: GraphData,
  startVertexId?: string
): ExecutionStep<ShortestPathState>[] {
  const steps: ExecutionStep<ShortestPathState>[] = [];
  const graph = cloneGraph(initialGraph);
  const start = startVertexId || graph.startVertexId || graph.vertices[0]?.id || 'A';
  const V = graph.vertices.length;

  const distances: Record<string, number> = {};
  const predecessors: Record<string, string | null> = {};
  graph.vertices.forEach((v) => {
    distances[v.id] = v.id === start ? 0 : Infinity;
    predecessors[v.id] = null;
  });

  const state: ShortestPathState = {
    algorithm: 'bellman-ford',
    graph,
    startVertex: start,
    distances: { ...distances },
    predecessors: { ...predecessors },
    currentVertex: null,
    settled: [],
    hasNegativeWeightCycle: false,
    message: `Bellman-Ford initialized with source ${start}. Will perform up to ${V - 1} relaxation passes.`,
  };

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `Bellman-Ford initialized: dist[${start}] = 0, all other vertices = ∞. Relaxing all edges ${V - 1} times.`,
    codeLine: 2,
    state: JSON.parse(JSON.stringify(state)),
    counters: { pass: 0 },
  });

  // Directed edges list
  const edgeList: { u: string; v: string; w: number }[] = [];
  graph.edges.forEach((e) => {
    edgeList.push({ u: e.source, v: e.target, w: e.weight });
    if (!graph.isDirected) {
      edgeList.push({ u: e.target, v: e.source, w: e.weight });
    }
  });

  for (let pass = 1; pass <= V - 1; pass++) {
    let anyChange = false;

    steps.push({
      stepIndex: steps.length,
      operationType: 'pass-start',
      description: `Starting Pass ${pass} of ${V - 1}. Checking all ${edgeList.length} edges.`,
      codeLine: 4,
      state: JSON.parse(JSON.stringify(state)),
      counters: { pass },
    });

    for (const edge of edgeList) {
      const { u, v, w } = edge;
      if (state.distances[u] !== Infinity && state.distances[u] + w < state.distances[v]) {
        state.distances[v] = state.distances[u] + w;
        state.predecessors[v] = u;
        anyChange = true;

        steps.push({
          stepIndex: steps.length,
          operationType: 'relax',
          description: `Pass ${pass}: Relaxed edge (${u} -> ${v}, w=${w}). New dist[${v}] = ${state.distances[v]}.`,
          codeLine: 7,
          state: JSON.parse(JSON.stringify(state)),
          counters: { pass },
        });
      }
    }

    if (!anyChange) {
      steps.push({
        stepIndex: steps.length,
        operationType: 'early-stop',
        description: `Pass ${pass}: No distances updated. Graph has converged early!`,
        codeLine: 9,
        state: JSON.parse(JSON.stringify(state)),
        counters: { pass },
      });
      break;
    }
  }

  // Pass V: Negative cycle detection
  let detectedCycle = false;
  for (const edge of edgeList) {
    const { u, v, w } = edge;
    if (state.distances[u] !== Infinity && state.distances[u] + w < state.distances[v]) {
      detectedCycle = true;
      state.hasNegativeWeightCycle = true;
      steps.push({
        stepIndex: steps.length,
        operationType: 'negative-cycle-detected',
        description: `NEGATIVE CYCLE DETECTED! Edge (${u} -> ${v}, w=${w}) can still be relaxed: ${state.distances[u]} + ${w} < ${state.distances[v]}.`,
        codeLine: 12,
        state: JSON.parse(JSON.stringify(state)),
        counters: { pass: V },
      });
      break;
    }
  }

  if (!detectedCycle) {
    steps.push({
      stepIndex: steps.length,
      operationType: 'complete',
      description: `Bellman-Ford finished successfully. No negative cycles detected. Final distances from ${start}: ${Object.entries(state.distances).map(([k, d]) => `${k}:${d}`).join(', ')}.`,
      codeLine: 14,
      state: JSON.parse(JSON.stringify(state)),
      counters: { pass: V - 1 },
    });
  }

  return steps;
}

// 3. FLOYD-WARSHALL ALGORITHM (All-Pairs Shortest Path)
export function generateFloydWarshallSteps(initialGraph: GraphData): ExecutionStep<ShortestPathState>[] {
  const steps: ExecutionStep<ShortestPathState>[] = [];
  const graph = cloneGraph(initialGraph);
  const labels = graph.vertices.map((v) => v.id);
  const n = labels.length;

  // Initialize distance matrix
  const dist: number[][] = Array.from({ length: n }, () => Array(n).fill(Infinity));
  for (let i = 0; i < n; i++) dist[i][i] = 0;

  const idxMap = new Map(labels.map((l, i) => [l, i]));
  graph.edges.forEach((e) => {
    const u = idxMap.get(e.source)!;
    const v = idxMap.get(e.target)!;
    dist[u][v] = Math.min(dist[u][v], e.weight);
    if (!graph.isDirected) {
      dist[v][u] = Math.min(dist[v][u], e.weight);
    }
  });

  const state: ShortestPathState = {
    algorithm: 'floyd-warshall',
    graph,
    startVertex: labels[0] || 'A',
    distances: {},
    predecessors: {},
    matrix: {
      labels,
      dist: dist.map((row) => row.map((val) => (val === Infinity ? '∞' : val))),
    },
    currentVertex: null,
    settled: [],
    hasNegativeWeightCycle: false,
    message: 'Floyd-Warshall initialized: D^(0) matrix created from direct edges.',
  };

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `Floyd-Warshall initialized for ${n} vertices. Running 3 nested loops (k=intermediate, i=source, j=dest).`,
    codeLine: 2,
    state: JSON.parse(JSON.stringify(state)),
  });

  for (let k = 0; k < n; k++) {
    const kLabel = labels[k];
    steps.push({
      stepIndex: steps.length,
      operationType: 'intermediate-k',
      description: `Considering intermediate vertex k = ${kLabel} (index ${k + 1}/${n}). Checking if paths through ${kLabel} are shorter.`,
      codeLine: 4,
      state: JSON.parse(JSON.stringify(state)),
    });

    for (let i = 0; i < n; i++) {
      for (let j = 0; j < n; j++) {
        if (dist[i][k] !== Infinity && dist[k][j] !== Infinity) {
          if (dist[i][k] + dist[k][j] < dist[i][j]) {
            dist[i][j] = dist[i][k] + dist[k][j];
            state.matrix = {
              labels,
              dist: dist.map((row) => row.map((val) => (val === Infinity ? '∞' : val))),
            };

            steps.push({
              stepIndex: steps.length,
              operationType: 'update-cell',
              description: `Relaxed path ${labels[i]} -> ${labels[j]} through ${kLabel}! New distance = ${dist[i][j]}.`,
              codeLine: 7,
              state: JSON.parse(JSON.stringify(state)),
            });
          }
        }
      }
    }
  }

  // Check diagonal for negative cycle
  let hasNegCycle = false;
  for (let i = 0; i < n; i++) {
    if (dist[i][i] < 0) {
      hasNegCycle = true;
      state.hasNegativeWeightCycle = true;
      break;
    }
  }

  steps.push({
    stepIndex: steps.length,
    operationType: 'complete',
    description: hasNegCycle
      ? 'Negative cycle detected! At least one diagonal entry dist[i][i] < 0.'
      : `Floyd-Warshall completed! Computed all-pairs shortest paths for all ${n} vertices in O(V^3) time.`,
    codeLine: 10,
    state: JSON.parse(JSON.stringify(state)),
  });

  return steps;
}
