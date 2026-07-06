# 2492. Minimum Score of a Path Between Two Cities

## Problem Statement

You are given a positive integer `n` representing `n` cities numbered from `1` to `n`.

You are also given a 2D array `roads` where each road is:

- `roads[i] = [a, b, distance]`
- This means there is a bidirectional road between city `a` and city `b` with weight `distance`.

The score of a path is the minimum road weight along that path.

Return the minimum possible score of a path from city `1` to city `n`.

---

## Key Idea

Since revisiting roads and cities is allowed, if city `1` and city `n` are in the same connected component, then we can use any road in that component while forming a path.

So the answer is simply:

- the minimum edge weight in the connected component containing city `1`.

---

## Approach

1. Build an adjacency list.
2. Start BFS/DFS from city `1`.
3. While traversing, track the minimum edge weight seen.
4. Return that minimum value.

---

## Java Solution

```java
class Solution {
    public int minScore(int n, int[][] roads) {
        List<List<int[]>> adj = new ArrayList<>();

        for (int i = 0; i <= n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] road : roads) {
            int u = road[0];
            int v = road[1];
            int w = road[2];

            adj.get(u).add(new int[]{v, w});
            adj.get(v).add(new int[]{u, w});
        }

        boolean[] visited = new boolean[n + 1];
        Queue<Integer> q = new LinkedList<>();
        q.offer(1);
        visited[1] = true;

        int answer = Integer.MAX_VALUE;

        while (!q.isEmpty()) {
            int node = q.poll();

            for (int[] edge : adj.get(node)) {
                int next = edge[0];
                int weight = edge[1];

                answer = Math.min(answer, weight);

                if (!visited[next]) {
                    visited[next] = true;
                    q.offer(next);
                }
            }
        }

        return answer;
    }
}
```

---

## Explanation

The path is allowed to contain the same road multiple times, so the path can effectively include any edge from the connected component.

That means the best possible score is the smallest edge weight inside the component containing city `1` and city `n`.

---

## Complexity

- Time Complexity: `O(n + m)`
- Space Complexity: `O(n + m)`

Where:
- `n` = number of cities
- `m` = number of roads
