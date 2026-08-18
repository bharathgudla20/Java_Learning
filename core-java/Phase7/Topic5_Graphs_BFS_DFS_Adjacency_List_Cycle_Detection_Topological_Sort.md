# Phase 7, Topic 5: Graphs — BFS & DFS

Graphs are the final major data structure, and these three subtopics form the foundation for almost every graph question. BFS and DFS are the two core traversal algorithms; cycle detection and topological sort are the two most common applications of those traversals.

---

## Why this matters

Graphs appear everywhere in real systems — social networks (connections between users), dependency resolution (what to build first), route-finding (shortest path), recommendation engines. Mastering BFS/DFS unlocks solutions to hundreds of problems, but the core idea is deceptively simple. Interviewers specifically watch for whether you can adapt the standard BFS/DFS template to new conditions (like tracking states, finding cycles, or ordering).

---

## 0. Graph Representations

Two main ways to store a graph — know both, and know the trade-offs:

```java
// Adjacency List — most common, space-efficient, especially for sparse graphs
Map<Integer, List<Integer>> adjList = new HashMap<>();
adjList.put(0, Arrays.asList(1, 2));
adjList.put(1, Arrays.asList(0, 3));
adjList.put(2, Arrays.asList(0, 3));
adjList.put(3, Arrays.asList(1, 2));

// Adjacency Matrix — good for dense graphs, faster to check "is there an edge?"
int[][] adjMatrix = new int[4][4];
adjMatrix[0][1] = 1; adjMatrix[1][0] = 1;  // undirected: set both directions
adjMatrix[0][2] = 1; adjMatrix[2][0] = 1;
// ... etc
```

**When to use which (important to state in interviews):**

- **Adjacency List:** space O(V + E), time to find neighbors O(degree). Best for sparse graphs (fewer edges).
- **Adjacency Matrix:** space O(V²), time to check if edge exists O(1). Best for dense graphs (many edges).

Most interview problems assume adjacency list representation (it's the default).

---

## 1. Adjacency List — Definition & Construction

**Definition:** A graph representation where each vertex stores a list of its neighbors — a HashMap/Array of Lists is the typical implementation.

**Why it's standard for DSA:**

- **Space-efficient:** only stores actual edges, not all possible pairs.
- **Natural traversal:** to explore from a node, you iterate its neighbor list — exactly what BFS/DFS need.
- **Flexible:** works for directed, undirected, weighted, unweighted graphs.

**Building an adjacency list from edges (very common setup in interviews):**

```java
public Map<Integer, List<Integer>> buildAdjList(int numNodes, int[][] edges) {
    Map<Integer, List<Integer>> adjList = new HashMap<>();

    // Initialize all nodes (avoid null-pointer issues)
    for (int i = 0; i < numNodes; i++) {
        adjList.put(i, new ArrayList<>());
    }

    // Add edges
    for (int[] edge : edges) {
        int u = edge[0], v = edge[1];
        adjList.get(u).add(v);      // directed: u → v
        // For undirected graphs, also add: adjList.get(v).add(u);
    }
    return adjList;
}
```

**Example:** `edges = [[0,1], [0,2], [1,3], [2,3]]` produces:

```text
0 → [1, 2]
1 → [0, 3]
2 → [0, 3]
3 → [1, 2]
```

---

## 2. BFS (Breadth-First Search)

**Definition:** Explore a graph level-by-level, visiting all neighbors of the current node before moving deeper — uses a Queue (FIFO).

**Visualization:** Expands outward in concentric "rings" from the starting node.

```java
public void bfs(Map<Integer, List<Integer>> adjList, int start) {
    Set<Integer> visited = new HashSet<>();
    Queue<Integer> queue = new LinkedList<>();

    queue.offer(start);
    visited.add(start);

    while (!queue.isEmpty()) {
        int node = queue.poll();
        System.out.println(node);  // process the node

        for (int neighbor : adjList.get(node)) {
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                queue.offer(neighbor);
            }
        }
    }
}
```

**Walkthrough on the earlier example graph, starting from node 0:**

- Queue: [0], Visited: {0}
- Pop 0 → neighbors [1,2] → add both → Queue: [1,2], Visited: {0,1,2}
- Pop 1 → neighbors [0,3] → 0 already visited, add 3 → Queue: [2,3], Visited: {0,1,2,3}
- Pop 2 → neighbors [0,3] → both already visited → Queue: [3], Visited: {0,1,2,3}
- Pop 3 → neighbors [1,2] → both already visited → Queue: [], done

**Why mark as visited BEFORE adding to queue (common mistake if you don't):** if you wait to mark visited after popping, the same node can be added to the queue multiple times by different neighbors — wastes space and time.

**BFS properties (important for interviews):**

- **Shortest path in unweighted graph:** BFS finds the minimum-hop path from source to target.
- **Time complexity:** O(V + E) — each node visited once, each edge examined once.
- **Space complexity:** O(V) for the queue and visited set.

---

## 3. DFS (Depth-First Search)

**Definition:** Explore a graph by going as far down one path as possible before backtracking — uses a Stack (or recursion, which implicitly uses the call stack).

**Visualization:** Plunges deep along one branch, backtracks when hitting a dead end.

**Recursive (most common implementation):**

```java
public void dfs(Map<Integer, List<Integer>> adjList, int node, Set<Integer> visited) {
    visited.add(node);
    System.out.println(node);  // process the node

    for (int neighbor : adjList.get(node)) {
        if (!visited.contains(neighbor)) {
            dfs(adjList, neighbor, visited);
        }
    }
}
// Call: dfs(adjList, 0, new HashSet<>());
```

**Iterative (using an explicit Stack):**

```java
public void dfsIterative(Map<Integer, List<Integer>> adjList, int start) {
    Set<Integer> visited = new HashSet<>();
    Deque<Integer> stack = new ArrayDeque<>();

    stack.push(start);
    visited.add(start);

    while (!stack.isEmpty()) {
        int node = stack.pop();
        System.out.println(node);

        for (int neighbor : adjList.get(node)) {
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                stack.push(neighbor);
            }
        }
    }
}
```

**Walkthrough (same graph, starting from 0):**

- Stack: [0], Visited: {0}
- Pop 0 → neighbors [1,2] → push both (order matters: stack is LIFO, so push 2 then 1 to visit 1 first) → Stack: [2,1], Visited: {0,1,2}
- Pop 1 → neighbors [0,3] → only add 3 → Stack: [2,3], Visited: {0,1,2,3}
- Pop 3 → neighbors [1,2] → both visited → Stack: [2]
- Pop 2 → neighbors [0,3] → both visited → Stack: [], done

**DFS properties:**

- **Time/Space complexity:** O(V + E) for time; O(V) for space (call stack in recursion).
- **Different traversal order than BFS:** explores deep before wide.
- **Better for cycle detection and topological sort** (explained next).

---

## 4. Cycle Detection

**Definition:** Determine if a graph contains a cycle (a path that starts and ends at the same node).

**Behavior differs by graph type:**

### Undirected Graph Cycle Detection

A cycle exists if you can reach a node through two different paths.

```java
public boolean hasCycleUndirected(Map<Integer, List<Integer>> adjList, int numNodes) {
    Set<Integer> visited = new HashSet<>();

    for (int i = 0; i < numNodes; i++) {
        if (!visited.contains(i)) {
            if (dfsHasCycleUndirected(adjList, i, -1, visited)) {
                return true;
            }
        }
    }
    return false;
}

private boolean dfsHasCycleUndirected(Map<Integer, List<Integer>> adjList, int node,
                                       int parent, Set<Integer> visited) {
    visited.add(node);

    for (int neighbor : adjList.get(node)) {
        if (!visited.contains(neighbor)) {
            if (dfsHasCycleUndirected(adjList, neighbor, node, visited)) {
                return true;
            }
        } else if (neighbor != parent) {
            // Found a visited node that's NOT the parent → cycle detected
            return true;
        }
    }
    return false;
}
```

**Key insight:** in an undirected graph, if you reach a visited node that's not your immediate parent, that's a back edge (an edge leading back to an ancestor) — which creates a cycle. The `parent` parameter is crucial: without it, you'd mistake the parent connection as a cycle.

**Example:** In `0—1—2—0` (a triangle):

- Visit 0, explore 1, explore 2, see 0 (visited, not parent of 2) → cycle found ✓

### Directed Graph Cycle Detection

More complex — a cycle exists if you ever revisit a node currently in the recursive call stack (a "back edge" in the recursion tree).

```java
public boolean hasCycleDirected(Map<Integer, List<Integer>> adjList, int numNodes) {
    int[] state = new int[numNodes];
    // state[i]: 0 = unvisited, 1 = visiting (in current DFS path), 2 = done

    for (int i = 0; i < numNodes; i++) {
        if (state[i] == 0) {
            if (dfsHasCycleDirected(adjList, i, state)) {
                return true;
            }
        }
    }
    return false;
}

private boolean dfsHasCycleDirected(Map<Integer, List<Integer>> adjList, int node, int[] state) {
    state[node] = 1;  // mark as "visiting"

    for (int neighbor : adjList.get(node)) {
        if (state[neighbor] == 1) {
            // Neighbor is currently in the recursion stack → back edge → cycle
            return true;
        } else if (state[neighbor] == 0) {
            if (dfsHasCycleDirected(adjList, neighbor, state)) {
                return true;
            }
        }
    }

    state[node] = 2;  // mark as "done"
    return false;
}
```

**Why three states are necessary (very important to explain):**

- **0 (unvisited):** never explored.
- **1 (visiting):** currently in the recursive call stack — if you see this on a neighbor, it's a back edge (cycle).
- **2 (done):** finished exploring, and found no cycle from here — safe to reuse this subtree's results.

**Walkthrough:** For `0→1→2→1` (cycle: 1→2→1):

- Visit 0 (state=1), visit 1 (state=1), visit 2 (state=1), visit 1 (state=1) → neighbor 1 is in visiting state → cycle found ✓

---

## 5. Topological Sort

**Definition:** Order the nodes of a directed acyclic graph (DAG) such that for every edge `u→v`, node `u` comes before `v` in the ordering — fundamental for dependency resolution (build systems, task scheduling, course prerequisites).

**Only works on DAGs** — if there's a cycle, topological sort is undefined (how do you order things that depend on each other circularly?). This is why cycle detection often precedes topological sort.

### Kahn's Algorithm (BFS-based, using in-degree)

```java
public List<Integer> topologicalSortKahn(Map<Integer, List<Integer>> adjList, int numNodes) {
    // Compute in-degree for each node
    int[] inDegree = new int[numNodes];
    for (int node = 0; node < numNodes; node++) {
        for (int neighbor : adjList.get(node)) {
            inDegree[neighbor]++;
        }
    }

    // Start with nodes that have in-degree 0 (no prerequisites)
    Queue<Integer> queue = new LinkedList<>();
    for (int i = 0; i < numNodes; i++) {
        if (inDegree[i] == 0) {
            queue.offer(i);
        }
    }

    List<Integer> result = new ArrayList<>();
    while (!queue.isEmpty()) {
        int node = queue.poll();
        result.add(node);

        // Remove edges from this node (it's now "processed")
        for (int neighbor : adjList.get(node)) {
            inDegree[neighbor]--;
            if (inDegree[neighbor] == 0) {
                queue.offer(neighbor);
            }
        }
    }

    // If not all nodes are in the result, there's a cycle
    if (result.size() != numNodes) {
        throw new IllegalArgumentException("Graph contains a cycle");
    }
    return result;
}
```

**Walkthrough on `0→1, 0→2, 1→3, 2→3`:**

- In-degrees: [0, 1, 1, 2]
- Start: queue = [0] (only in-degree 0)
- Pop 0, add to result → reduce in-degrees of 1 and 2 → [1, 0, 0, 2] → queue = [1, 2]
- Pop 1, add to result → reduce in-degree of 3 → [1, 0, 0, 1] → queue = [2]
- Pop 2, add to result → reduce in-degree of 3 → [1, 0, 0, 0] → queue = [3]
- Pop 3, add to result → queue = []
- Result: [0, 1, 2, 3] ✓ (all nodes included, so no cycle)

### DFS-based approach

```java
public List<Integer> topologicalSortDFS(Map<Integer, List<Integer>> adjList, int numNodes) {
    int[] state = new int[numNodes];  // 0=unvisited, 1=visiting, 2=done
    Deque<Integer> stack = new ArrayDeque<>();

    for (int i = 0; i < numNodes; i++) {
        if (state[i] == 0) {
            if (dfsTopoSort(adjList, i, state, stack)) {
                throw new IllegalArgumentException("Graph contains a cycle");
            }
        }
    }

    return new ArrayList<>(stack);
}

private boolean dfsTopoSort(Map<Integer, List<Integer>> adjList, int node, int[] state, Deque<Integer> stack) {
    state[node] = 1;

    for (int neighbor : adjList.get(node)) {
        if (state[neighbor] == 1) return true;  // back edge → cycle
        if (state[neighbor] == 0) {
            if (dfsTopoSort(adjList, neighbor, state, stack)) return true;
        }
    }

    state[node] = 2;
    stack.push(node);  // add to result when DONE (post-order)
    return false;
}
```

**Key insight (why it works):** Push nodes onto the stack after finishing their exploration (post-order DFS) — this ensures dependencies come before dependents.

---

## Quick Comparison Table

| Algorithm | Data Structure | Order | Typical Use | Time/Space |
|----------|----------------|-------|-------------|------------|
| BFS | Queue | Level-by-level | Shortest path in unweighted graph | O(V + E) |
| DFS | Stack / Recursion | Depth-first | Cycle detection, connected components | O(V + E) |
| Topological Sort (Kahn) | Queue + in-degree | Dependency order | DAG scheduling | O(V + E) |
| Topological Sort (DFS) | Recursion + states | Post-order | DAG ordering with DFS | O(V + E) |

---

## Final Takeaway

- BFS is for expanding outward level by level.
- DFS is for going deep before backtracking.
- Cycle detection is a natural DFS application, with different rules for directed vs. undirected graphs.
- Topological sort is the canonical DAG ordering technique; Kahn’s algorithm and DFS both work, but Kahn’s is easier to reason about for interviews.

The real skill is not memorizing templates — it is recognizing which graph property you need to track: visited states, parents, in-degrees, or recursion stack states.
