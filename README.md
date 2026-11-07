# AlgoJ — Interactive Data Structures & Algorithms Visualizer

**AlgoJ** is a full-stack Data Structures and Algorithms visualizer and educational platform built with a bold **Neo-Brutalist** aesthetic.

The application features:
- **Authoritative Algorithm Execution in Java**: All data structures, sorting algorithms, searching routines, tree traversals, graph algorithms, recursion, and dynamic programming engines are implemented authoritatively in **Java 17 with Spring Boot**.
- **Execution Event Tracing**: The Java backend runs genuine algorithms and generates ordered execution event traces with detailed state snapshots, comparison counters, active pseudocode line numbers, and human-readable step explanations.
- **Client-Side Rendering & Time-Travel Animation**: The React 19 + TypeScript frontend receives the Java execution trace over a REST API and provides smooth playback controls, bidirectional timeline scrubbing, operational counters, and side-by-side algorithm comparisons.
- **Graceful Offline Fallback**: If the Java backend is temporarily offline, the frontend seamlessly uses a client-side execution engine without crashing.

---

## 🏗️ Architecture Overview

```mermaid
graph TD
    Client["React + TypeScript Frontend\n(Port 5173)\n- Neo-Brutalist Visualizers\n- Reversible Playback Engine\n- Learning & Comparison Arena"]
    API["REST API (JSON over HTTP)\n- CORS Configured\n- Request Validation"]
    Backend["Java Spring Boot Backend\n(Port 8080 · OpenJDK 17)\n- SortingEngine\n- SearchingEngine\n- LinearStructureEngine\n- Tree & Heap Engines\n- GraphAlgorithmEngine\n- Recursion & DPEngine"]

    Client -->|POST /api/algorithms/run| API
    Client -->|POST /api/structures/operate| API
    Client -->|GET /api/health| API
    API --> Backend
    Backend -->|Ordered Execution Trace (ExecutionStep JSON)| Client
```

---

## 🎨 Visual Identity & Design System

- **Neo-Brutalist Visuals**: Warm cream (`#FAF6EC`) background, 2.5px solid dark ink borders (`#1A1A1A`), tactile drop shadows (`4px 4px 0 #1A1A1A`), rounded cards (`rounded-3xl`), and multi-point starburst accents.
- **Harmonious Pastel Blocking**: Lavender (`#C9B8F5`), Butter Yellow (`#F5D876`), Soft Mint Green (`#8FD6B0`), Warm Orange (`#F5A868`), and Crisp White (`#FFFFFF`).
- **Typography**: Display typography using `Outfit` combined with monospace code in `JetBrains Mono`.
- **Live Status Indicator**: Visual pill displaying real-time connection status with the Java Spring Boot backend (`Java Backend: Connected (Spring Boot · OpenJDK 17)`).

---

## ☕ Java Backend Implementation

All core DSA logic is implemented in Java under `backend/src/main/java/com/algoj/`:

### 1. Sorting Algorithms (`com.algoj.engine.sorting.SortingEngine`)
- **Bubble Sort**: Adjacent element comparisons, swap animations, early termination optimization.
- **Selection Sort**: Unsorted boundary scan, minimum element tracking, single swap per pass.
- **Insertion Sort**: Sorted subarray expansion, element shifts, key insertion.
- **Merge Sort**: Divide-and-conquer subarray splitting and two-pointer buffer merging.
- **Quick Sort**: Pivot selection (Lomuto scheme), two-way partition boundaries, in-place swaps.
- **Heap Sort**: Bottom-up Max-Heap construction, root-to-end extractions, sift-down heapify.

### 2. Searching Algorithms (`com.algoj.engine.searching.SearchingEngine`)
- **Linear Search**: Sequential element scan, match and not-found states.
- **Binary Search**: Sorted input validation, midpoint calculation, low/high bound elimination intervals.

### 3. Linear Data Structures (`com.algoj.engine.structures`)
- **Arrays (`ArrayStructureEngine`)**: Indexed lookup O(1), updates, insertions with rightward shifts, deletions with leftward shifts, traversals.
- **Linked Lists (`LinkedListStructureEngine`)**: Singly and doubly linked lists; insert/delete at head, tail, or position; full in-place list reversal; pointer redirects (`next`, `prev`, `null`).
- **Stacks (`StackStructureEngine`)**: LIFO push, pop, peek, isEmpty, capacity limits with underflow and overflow protection.
- **Queues (`QueueStructureEngine`)**: FIFO enqueue, dequeue, peek front, circular queue modulo wraparound `(rear + 1) % capacity`.
- **Hash Tables (`HashTableStructureEngine`)**: Key-value mapping via hash function; collision resolution via **Separate Chaining** or **Linear Probing** with deleted tombstones.

### 4. Trees & Heaps (`com.algoj.engine.structures`)
- **Binary Search Trees (`BSTStructureEngine`)**: Insertions, searches, deletions (leaf, 1-child, 2-children with inorder successor), traversals (Preorder, Inorder, Postorder, Level-order).
- **Binary Heaps (`HeapStructureEngine`)**: Min-Heap and Max-Heap modes; sift-up and sift-down operations.
- **AVL Trees (`AVLStructureEngine`)**: Self-balancing BST with node heights and balance factors `BF ∈ {-1, 0, 1}`; automated rotations (LL, RR, LR, RL).
- **Tries (`TrieStructureEngine`)**: Prefix trees; string insertions, full-word search, prefix search (`startsWith`), and branch deletions preserving shared stems.

### 5. Graph Algorithms (`com.algoj.engine.graphs.GraphAlgorithmEngine`)
- **Breadth-First Search (BFS)**: FIFO queue display, layer-by-layer exploration, disconnected component detection.
- **Depth-First Search (DFS)**: Recursive call stack tracking, path exploration, backtracking.
- **Dijkstra's Algorithm**: Min-distance tentative table, priority queue relaxation, negative weight edge warnings.
- **Bellman-Ford Algorithm**: Edge relaxation across V-1 passes, reachable negative cycle detection on V-th pass.
- **Floyd-Warshall Algorithm**: All-pairs shortest path dynamic programming matrix `D[i][j]` over intermediate vertices `k`.
- **Kruskal's Algorithm**: Sorted edge evaluation, Disjoint Set Union (DSU) cycle rejection, Spanning Forest for disconnected graphs.
- **Prim's Algorithm**: Minimum crossing cut edge expansion, spanning tree/forest formation.

### 6. Recursion & Dynamic Programming (`com.algoj.engine.recursion` & `com.algoj.engine.dp`)
- **Factorial**: Call stack frame creation, base case `n <= 1`, stack unwinding.
- **Fibonacci**: Side-by-side comparison of **Naive Recursion** O(2^n), **Memoization** (Top-Down DP) with cache hits, and **Tabulation** (Bottom-Up DP).
- **Tower of Hanoi**: Recursive 3-peg disk movement, exact `2^n - 1` move counting.
- **N-Queens**: N×N chessboard backtracking, column and diagonal conflict detection, queen placement and removal.
- **0/1 Knapsack**: 2D DP table `(N+1) × (W+1)`, dependency cell highlights, item backtracking.
- **Longest Common Subsequence (LCS)**: 2D table match/mismatch transitions, string reconstruction arrows.

---

## 📡 REST API Contracts

### Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/health` | Backend status check, Java version, uptime |
| `GET` | `/api/catalog` | List of all supported DSA categories and IDs |
| `POST` | `/api/algorithms/run` | Execute algorithm and generate execution trace |
| `POST` | `/api/structures/operate` | Execute interactive data structure operation |
| `GET` | `/api/algorithms/sample-graph` | Fetch predefined graph datasets |
| `GET` | `/api/structures/initial/{id}` | Get initial data structure state |

### Sample Request: `POST /api/algorithms/run`
```json
{
  "algorithmId": "bubble-sort",
  "array": [35, 12, 48, 22, 60, 5]
}
```

### Sample Response: `ExecutionTraceResponse`
```json
{
  "algorithmId": "bubble-sort",
  "totalSteps": 24,
  "steps": [
    {
      "stepIndex": 0,
      "totalSteps": 24,
      "operationType": "init",
      "description": "Initial unsorted array loaded.",
      "codeLine": 1,
      "state": {
        "array": [
          { "id": "item-0-35", "value": 35, "state": "default" },
          { "id": "item-1-12", "value": 12, "state": "default" }
        ],
        "comparingIndices": [],
        "swappingIndices": [],
        "pivotIndex": null,
        "sortedIndices": [],
        "comparisons": 0,
        "swaps": 0,
        "writes": 0
      },
      "counters": {
        "comparisons": 0,
        "swaps": 0,
        "writes": 0
      }
    }
  ],
  "summary": {
    "algorithmId": "bubble-sort",
    "totalSteps": 24,
    "completedSuccessfully": true
  }
}
```

---

## 🛠️ Setup & Running Instructions

### System Requirements
- **Java**: OpenJDK 17 or higher
- **Maven**: Maven 3.8+ (binary available in `~/.maven/` or system `mvn`)
- **Node.js**: Node 18+ and npm 9+

---

### 1. Running the Java Spring Boot Backend

From the repository root:
```bash
cd backend
mvn clean package -DskipTests
java -jar target/algoj-backend-1.0.0.jar
```
Or directly using Maven:
```bash
cd backend
mvn spring-boot:run
```
The backend starts on `http://localhost:8080/`. You can verify with:
```bash
curl http://localhost:8080/api/health
```

---

### 2. Running the React + TypeScript Frontend

From the repository root:
```bash
npm install
npm run dev
```
The frontend dev server starts on `http://localhost:5173/`. Open it in any modern web browser.

---

### 3. Running Automated Tests

#### Java Backend Unit & Integration Tests (JUnit 5 + MockMvc)
```bash
cd backend
mvn test
```
*Executes 34 test cases verifying algorithm correctness, edge cases, data structure invariants, service layer orchestration, and REST API controller integration.*

#### Frontend Unit Tests (Vitest)
```bash
npm test
```
*Executes 91 test cases verifying playback mechanics, timeline scrub logic, and local fallbacks.*

#### Production Build Verification
```bash
npm run build
```
*Compiles TypeScript and bundles production assets via Vite.*
