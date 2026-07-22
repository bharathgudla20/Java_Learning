# Shift 2D Grid

## Problem Statement

Given a 2D `grid` of size `m x n` and an integer `k`, shift the grid `k` times.

In one shift operation:

- The element at `grid[i][j]` moves to `grid[i][j + 1]`.
- The element at `grid[i][n - 1]` moves to `grid[i + 1][0]`.
- The element at `grid[m - 1][n - 1]` moves to `grid[0][0]`.

Return the 2D grid after applying the shift operation `k` times.

## Approach

Treat the entire grid as a flattened array without actually creating one.

Let:

- `m` = number of rows
- `n` = number of columns
- `total` = total number of elements, `m * n`

For every cell:

1. Reduce `k` using modulo because shifting by `total` positions returns the grid to its original state.
2. Convert the cell's `(row, column)` position into a one-dimensional index:
   `oldIndex = row * n + column`
3. Compute its new index after shifting:
   `newIndex = (oldIndex + k) % total`
4. Convert the new one-dimensional index back into a `(row, column)` position.
5. Store the value in the answer grid.

This shifts the complete grid in one pass with $O(mn)$ time and $O(mn)$ extra space.

## Java Solution

```java
class Solution {
    public List<List<Integer>> shiftGrid(int[][] grid, int k) {

        int m = grid.length;
        int n = grid[0].length;

        int total = m * n;

        k %= total;

        List<List<Integer>> ans = new ArrayList<>();

        for (int i = 0; i < m; i++) {
            List<Integer> row = new ArrayList<>();

            for (int j = 0; j < n; j++) {
                row.add(0);
            }

            ans.add(row);
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {

                int oldIndex = i * n + j;

                int newIndex = (oldIndex + k) % total;

                int newRow = newIndex / n;
                int newCol = newIndex % n;

                ans.get(newRow).set(newCol, grid[i][j]);
            }
        }

        return ans;
    }
}
```
