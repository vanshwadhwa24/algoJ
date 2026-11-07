import { ExecutionStep } from '../../types/execution';
import { GraphData, GraphEdge, cloneGraph } from '../structures/graph';

export interface MSTState {
  algorithm: 'kruskal' | 'prim';
  graph: GraphData;
  mstEdges: GraphEdge[];
  totalWeight: number;
  currentEdge?: GraphEdge;
  rejectedEdges: GraphEdge[];
  isSpanningForest: boolean;
  componentsCount: number;
  message: string;
}

// Disjoint Set Union (DSU) helper
class DisjointSetUnion {
  parent: Record<string, string> = {};
  rank: Record<string, number> = {};

  constructor(elements: string[]) {
    elements.forEach((el) => {
      this.parent[el] = el;
      this.rank[el] = 0;
    });
  }

  find(i: string): string {
    if (this.parent[i] === i) return i;
    this.parent[i] = this.find(this.parent[i]); // path compression
    return this.parent[i];
  }

  union(i: string, j: string): boolean {
    const rootI = this.find(i);
    const rootJ = this.find(j);
    if (rootI === rootJ) return false; // same set -> cycle

    if (this.rank[rootI] < this.rank[rootJ]) {
      this.parent[rootI] = rootJ;
    } else if (this.rank[rootI] > this.rank[rootJ]) {
      this.parent[rootJ] = rootI;
    } else {
      this.parent[rootJ] = rootI;
      this.rank[rootI]++;
    }
    return true;
  }
}

// 1. KRUSKAL'S ALGORITHM
export function generateKruskalSteps(initialGraph: GraphData): ExecutionStep<MSTState>[] {
  const steps: ExecutionStep<MSTState>[] = [];
  const graph = cloneGraph(initialGraph);
  const vertexIds = graph.vertices.map((v) => v.id);

  // Sort edges ascending by weight
  const sortedEdges = [...graph.edges].sort((a, b) => a.weight - b.weight);
  const dsu = new DisjointSetUnion(vertexIds);

  const state: MSTState = {
    algorithm: 'kruskal',
    graph,
    mstEdges: [],
    totalWeight: 0,
    rejectedEdges: [],
    isSpanningForest: false,
    componentsCount: vertexIds.length,
    message: `Kruskal initialized: Sorted ${sortedEdges.length} edges by weight. Initialized Disjoint Set Union.`,
  };

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `Kruskal's algorithm initialized. Edges sorted: [${sortedEdges.map((e) => `(${e.source}-${e.target}:${e.weight})`).join(', ')}].`,
    codeLine: 2,
    state: JSON.parse(JSON.stringify(state)),
    counters: { mstEdgesCount: 0, totalWeight: 0 },
  });

  for (const edge of sortedEdges) {
    state.currentEdge = edge;
    const edgeInGraph = state.graph.edges.find((e) => e.id === edge.id);
    if (edgeInGraph) edgeInGraph.state = 'active';

    const rootU = dsu.find(edge.source);
    const rootV = dsu.find(edge.target);

    steps.push({
      stepIndex: steps.length,
      operationType: 'inspect-edge',
      description: `Examining edge (${edge.source} - ${edge.target}, w=${edge.weight}). Finding set roots: Find(${edge.source}) = ${rootU}, Find(${edge.target}) = ${rootV}.`,
      codeLine: 5,
      state: JSON.parse(JSON.stringify(state)),
      counters: { mstEdgesCount: state.mstEdges.length, totalWeight: state.totalWeight },
    });

    if (rootU !== rootV) {
      dsu.union(edge.source, edge.target);
      state.mstEdges.push(edge);
      state.totalWeight += edge.weight;
      state.componentsCount--;
      if (edgeInGraph) edgeInGraph.state = 'mst';

      steps.push({
        stepIndex: steps.length,
        operationType: 'add-mst-edge',
        description: `Roots differ (${rootU} != ${rootV})! Adding edge to MST without creating a cycle. Total MST weight is now ${state.totalWeight}.`,
        codeLine: 7,
        state: JSON.parse(JSON.stringify(state)),
        counters: { mstEdgesCount: state.mstEdges.length, totalWeight: state.totalWeight },
      });
    } else {
      state.rejectedEdges.push(edge);
      if (edgeInGraph) edgeInGraph.state = 'rejected';

      steps.push({
        stepIndex: steps.length,
        operationType: 'reject-edge',
        description: `CYCLE DETECTED! Both vertices belong to set ${rootU}. Rejecting edge (${edge.source} - ${edge.target}).`,
        codeLine: 9,
        state: JSON.parse(JSON.stringify(state)),
        counters: { mstEdgesCount: state.mstEdges.length, totalWeight: state.totalWeight },
      });
    }

    state.currentEdge = undefined;
  }

  // Count unique roots to detect disconnected graph
  const uniqueRoots = new Set(vertexIds.map((v) => dsu.find(v)));
  const isDisconnected = uniqueRoots.size > 1;
  state.isSpanningForest = isDisconnected;

  steps.push({
    stepIndex: steps.length,
    operationType: 'complete',
    description: isDisconnected
      ? `Kruskal finished! Graph is DISCONNECTED into ${uniqueRoots.size} components. Result is a Minimum Spanning FOREST with ${state.mstEdges.length} edges and total weight ${state.totalWeight}.`
      : `Kruskal complete! Found single connected Minimum Spanning Tree with ${state.mstEdges.length} edges and total weight ${state.totalWeight}.`,
    codeLine: 12,
    state: JSON.parse(JSON.stringify(state)),
    counters: { mstEdgesCount: state.mstEdges.length, totalWeight: state.totalWeight },
  });

  return steps;
}

// 2. PRIM'S ALGORITHM
export function generatePrimSteps(
  initialGraph: GraphData,
  startVertexId?: string
): ExecutionStep<MSTState>[] {
  const steps: ExecutionStep<MSTState>[] = [];
  const graph = cloneGraph(initialGraph);
  const start = startVertexId || graph.startVertexId || graph.vertices[0]?.id || 'A';
  const V = graph.vertices.length;

  const inMST = new Set<string>([start]);
  const mstEdges: GraphEdge[] = [];
  let totalWeight = 0;

  const state: MSTState = {
    algorithm: 'prim',
    graph,
    mstEdges: [],
    totalWeight: 0,
    rejectedEdges: [],
    isSpanningForest: false,
    componentsCount: V,
    message: `Prim's algorithm initialized with starting root vertex ${start}.`,
  };

  const startV = state.graph.vertices.find((v) => v.id === start);
  if (startV) startV.state = 'visited';

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `Added start vertex ${start} to MST cut. In-tree set: {${start}}.`,
    codeLine: 2,
    state: JSON.parse(JSON.stringify(state)),
    counters: { mstEdgesCount: 0, totalWeight: 0 },
  });

  while (inMST.size < V) {
    // Find minimum weight edge connecting a vertex in MST to a vertex outside MST
    let minEdge: GraphEdge | null = null;
    let minWeight = Infinity;

    for (const edge of state.graph.edges) {
      const uIn = inMST.has(edge.source);
      const vIn = inMST.has(edge.target);

      // Exactly one end in MST
      if ((uIn && !vIn) || (!uIn && vIn)) {
        if (edge.weight < minWeight) {
          minWeight = edge.weight;
          minEdge = edge;
        }
      }
    }

    if (!minEdge) {
      // Disconnected graph
      break;
    }

    const newVertex = inMST.has(minEdge.source) ? minEdge.target : minEdge.source;
    inMST.add(newVertex);
    mstEdges.push(minEdge);
    totalWeight += minEdge.weight;

    minEdge.state = 'mst';
    const newVNode = state.graph.vertices.find((v) => v.id === newVertex);
    if (newVNode) newVNode.state = 'visited';

    state.mstEdges = [...mstEdges];
    state.totalWeight = totalWeight;

    steps.push({
      stepIndex: steps.length,
      operationType: 'grow-tree',
      description: `Selected minimum crossing cut edge (${minEdge.source} - ${minEdge.target}, w=${minEdge.weight}). Added vertex ${newVertex} to MST. Total weight: ${totalWeight}.`,
      codeLine: 6,
      state: JSON.parse(JSON.stringify(state)),
      counters: { mstEdgesCount: mstEdges.length, totalWeight },
    });
  }

  const isDisconnected = inMST.size < V;
  state.isSpanningForest = isDisconnected;

  steps.push({
    stepIndex: steps.length,
    operationType: 'complete',
    description: isDisconnected
      ? `Prim's algorithm finished. Graph is DISCONNECTED (${V - inMST.size} vertex/vertices unreachable). Result is a partial MST forest of weight ${totalWeight}.`
      : `Prim's algorithm complete! Built Minimum Spanning Tree connecting all ${V} vertices with total weight ${totalWeight}.`,
    codeLine: 10,
    state: JSON.parse(JSON.stringify(state)),
    counters: { mstEdgesCount: mstEdges.length, totalWeight },
  });

  return steps;
}
