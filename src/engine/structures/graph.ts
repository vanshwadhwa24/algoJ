export interface GraphVertex {
  id: string;
  label: string;
  x: number;
  y: number;
  state?: 'default' | 'active' | 'visited' | 'queued' | 'current';
}

export interface GraphEdge {
  id: string;
  source: string;
  target: string;
  weight: number;
  directed: boolean;
  state?: 'default' | 'active' | 'traversed' | 'mst' | 'relaxed' | 'rejected';
}

export interface GraphData {
  vertices: GraphVertex[];
  edges: GraphEdge[];
  isDirected: boolean;
  isWeighted: boolean;
  startVertexId?: string;
}

export function cloneGraph(graph: GraphData): GraphData {
  return {
    isDirected: graph.isDirected,
    isWeighted: graph.isWeighted,
    startVertexId: graph.startVertexId,
    vertices: graph.vertices.map((v) => ({ ...v })),
    edges: graph.edges.map((e) => ({ ...e })),
  };
}

// Preset standard graph
export function getSampleGraph(): GraphData {
  const vertices: GraphVertex[] = [
    { id: 'A', label: 'A', x: 120, y: 150 },
    { id: 'B', label: 'B', x: 260, y: 70 },
    { id: 'C', label: 'C', x: 260, y: 230 },
    { id: 'D', label: 'D', x: 420, y: 70 },
    { id: 'E', label: 'E', x: 420, y: 230 },
    { id: 'F', label: 'F', x: 560, y: 150 },
  ];

  const edges: GraphEdge[] = [
    { id: 'e1', source: 'A', target: 'B', weight: 4, directed: false },
    { id: 'e2', source: 'A', target: 'C', weight: 2, directed: false },
    { id: 'e3', source: 'B', target: 'C', weight: 1, directed: false },
    { id: 'e4', source: 'B', target: 'D', weight: 5, directed: false },
    { id: 'e5', source: 'C', target: 'E', weight: 8, directed: false },
    { id: 'e6', source: 'C', target: 'D', weight: 8, directed: false },
    { id: 'e7', source: 'D', target: 'E', weight: 2, directed: false },
    { id: 'e8', source: 'D', target: 'F', weight: 6, directed: false },
    { id: 'e9', source: 'E', target: 'F', weight: 3, directed: false },
  ];

  return {
    vertices,
    edges,
    isDirected: false,
    isWeighted: true,
    startVertexId: 'A',
  };
}

export function getDisconnectedSampleGraph(): GraphData {
  const g = getSampleGraph();
  // Add a disconnected component G - H
  g.vertices.push({ id: 'G', label: 'G', x: 680, y: 90 });
  g.vertices.push({ id: 'H', label: 'H', x: 680, y: 210 });
  g.edges.push({ id: 'e10', source: 'G', target: 'H', weight: 7, directed: false });
  return g;
}

export function getNegativeWeightsSampleGraph(): GraphData {
  return {
    vertices: [
      { id: 'S', label: 'S', x: 120, y: 150 },
      { id: 'A', label: 'A', x: 280, y: 80 },
      { id: 'B', label: 'B', x: 280, y: 220 },
      { id: 'C', label: 'C', x: 460, y: 80 },
      { id: 'D', label: 'D', x: 460, y: 220 },
    ],
    edges: [
      { id: 'e1', source: 'S', target: 'A', weight: 4, directed: true },
      { id: 'e2', source: 'S', target: 'B', weight: 5, directed: true },
      { id: 'e3', source: 'A', target: 'C', weight: -2, directed: true },
      { id: 'e4', source: 'B', target: 'C', weight: 3, directed: true },
      { id: 'e5', source: 'C', target: 'D', weight: 2, directed: true },
      { id: 'e6', source: 'B', target: 'D', weight: 6, directed: true },
    ],
    isDirected: true,
    isWeighted: true,
    startVertexId: 'S',
  };
}

export function getAdjacencyList(graph: GraphData): Record<string, { target: string; weight: number }[]> {
  const adj: Record<string, { target: string; weight: number }[]> = {};
  graph.vertices.forEach((v) => {
    adj[v.id] = [];
  });

  graph.edges.forEach((e) => {
    if (adj[e.source]) adj[e.source].push({ target: e.target, weight: e.weight });
    if (!graph.isDirected && adj[e.target]) {
      adj[e.target].push({ target: e.source, weight: e.weight });
    }
  });

  return adj;
}

export function getAdjacencyMatrix(graph: GraphData): { labels: string[]; matrix: (number | null)[][] } {
  const labels = graph.vertices.map((v) => v.id);
  const n = labels.length;
  const matrix: (number | null)[][] = Array.from({ length: n }, () => Array(n).fill(null));

  for (let i = 0; i < n; i++) {
    matrix[i][i] = 0;
  }

  const indexMap = new Map(labels.map((l, i) => [l, i]));

  graph.edges.forEach((e) => {
    const u = indexMap.get(e.source);
    const v = indexMap.get(e.target);
    if (u !== undefined && v !== undefined) {
      matrix[u][v] = e.weight;
      if (!graph.isDirected) {
        matrix[v][u] = e.weight;
      }
    }
  });

  return { labels, matrix };
}
