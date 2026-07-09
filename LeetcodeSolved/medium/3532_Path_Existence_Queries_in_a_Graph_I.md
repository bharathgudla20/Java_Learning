# 3532. Path Existence Queries in a Graph I

## Problem Statement

You are given an integer `n` representing the number of nodes in a graph, labeled from `0` to `n - 1`.

You are also given a sorted integer array `nums` of length `n` in non-decreasing order and an integer `maxDiff`.

An undirected edge exists between nodes `i` and `j` if:

```java
Math.abs(nums[i] - nums[j]) <= maxDiff
```

For each query `[u, v]`, determine whether there is a path between `u` and `v`.

Return a boolean array `answer` where `answer[i]` is `true` if the corresponding query is connected.

---

## Example

### Example 1

```java
n = 2
nums = [1, 3]
maxDiff = 1
queries = [[0, 0], [0, 1]]
```

Output:

```java
[true, false]
```

---

## Key Idea

Because `nums` is sorted, the graph is naturally split into connected components by barriers.

If:

```java
nums[i] - nums[i - 1] > maxDiff
```

then there is no edge crossing from `i - 1` to `i`, so the nodes must belong to different components.

So we can assign a component number to each index:

- same component number => connected
- different component number => not connected

---

## Approach 1: Component Labeling (Optimal)

### Intuition

Since the array is sorted, every adjacent pair that exceeds `maxDiff` forms a boundary between components.

That means we only need to scan the array once and assign component IDs.

### Algorithm

1. Create an array `comp` of size `n`.
2. Set `comp[0] = 0`.
3. Traverse from index `1` to `n - 1`.
4. If `nums[i] - nums[i - 1] <= maxDiff`, then `comp[i] = comp[i - 1]`.
5. Otherwise, increase the component count and assign a new component id.
6. For each query, compare `comp[u]` and `comp[v]`.

---

## Java Solution

```java
class Solution {
    public boolean[] pathExistenceQueries(int n, int[] nums, int maxDiff, int[][] queries) {
        boolean[] ans = new boolean[queries.length];
        int[] comp = new int[n];

        int component = 0;

        comp[0] = 0;
        for (int i = 1; i < n; i++) {
            if (nums[i] - nums[i - 1] <= maxDiff) {
                comp[i] = component;
            } else {
                component++;
                comp[i] = component;
            }
        }

        for (int i = 0; i < queries.length; i++) {
            int u = queries[i][0];
            int v = queries[i][1];
            ans[i] = (comp[u] == comp[v]);
        }

        return ans;
    }
}
```

---

## Why This Works

The graph is formed by connecting values that differ by at most `maxDiff`.

Because the values are sorted, once a gap greater than `maxDiff` appears, no node on the left can connect to any node on the right through that boundary.

So the array is split into groups of nodes that are mutually connected, and each query is answered by checking whether both nodes belong to the same group.

---

## Complexity Analysis

- Time Complexity: `O(n + q)`
  - `O(n)` for assigning components
  - `O(q)` for answering queries
- Space Complexity: `O(n)`
  - for the `comp` array

Where:
- `n` = number of nodes
- `q` = number of queries

---

## Approach 2: DSU (Disjoint Set Union)

A more general approach is to use DSU.

### Idea

- Initially, every node is its own parent.
- For each adjacent pair, if the difference is at most `maxDiff`, union them.
- Then for each query, check whether `u` and `v` have the same root.

### Complexity

- Time Complexity: `O(n + q)`
- Space Complexity: `O(n)`

This approach is slightly more general, but the component-labeling method is simpler here because the array is sorted.
