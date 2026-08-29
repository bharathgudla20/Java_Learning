# Phase 7, Topic 8: Recursion & Backtracking — Subsets, Permutations, N-Queens, Word Search

This is the final topic in Phase 7 — once you're through this, all of DSA prep is complete and you'll move into Phase 8 (Spring Boot & Microservices). Backtracking is where recursion meets "try everything, undo what doesn't work" — a very common pattern for "generate all possibilities" style questions.

### Why this matters

While DP (last topic) optimizes toward a single best answer, backtracking is for problems that need to explore all valid possibilities — subsets, arrangements, placements. The core skill interviewers test: can you build the "choose → explore → un-choose" template and adapt it to different constraints?

---

### 1. The Core Idea — Backtracking Template

**Definition:** Backtracking is a refined brute-force technique that builds candidates for a solution incrementally, and abandons ("backtracks") a candidate as soon as it determines that candidate can't lead to a valid solution — instead of exploring it fully.

**The universal template (memorize this shape — nearly every backtracking problem fits it):**

```java
void backtrack(State current, Choices remaining) {
    if (isComplete(current)) {
        result.add(new ArrayList<>(current));   // record a valid solution
        return;
    }
    for (Choice choice : remaining) {
        current.add(choice);          // 1. CHOOSE
        backtrack(current, updatedRemaining);  // 2. EXPLORE
        current.remove(current.size() - 1);    // 3. UN-CHOOSE (backtrack)
    }
}
```

**Why the "un-choose" step is essential (the #1 mistake candidates make):** if you forget to remove the choice after exploring it, the `current` list stays polluted for the next iteration of the loop — every subsequent branch sees stale data from a path that's already been abandoned.

---

### 2. Subsets (Power Set)

**Definition:** Generate all possible subsets of a given set (including the empty set and the set itself) — for a set of size `n`, there are exactly `2ⁿ` subsets.

```java
public List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, 0, new ArrayList<>(), result);
    return result;
}

private void backtrack(int[] nums, int start, List<Integer> current, List<List<Integer>> result) {
    result.add(new ArrayList<>(current));   // every state along the way IS a valid subset

    for (int i = start; i < nums.length; i++) {
        current.add(nums[i]);                          // choose
        backtrack(nums, i + 1, current, result);        // explore (start from i+1, not 0 — avoids reusing earlier elements)
        current.remove(current.size() - 1);             // un-choose
    }
}
```

**Walkthrough on `[1, 2, 3]`:**

```
[] 
[1] → [1,2] → [1,2,3]
    → [1,3]
[2] → [2,3]
[3]
```

Every node visited in this recursion tree is added to the result — that's why `result.add()` happens before the loop, unlike Knapsack/LCS where you only record at a "complete" base case.

**Key difference from the general template:** here, every partial state is a valid answer (not just complete ones) — subsets don't need to use all elements.

---

### 3. Permutations

**Definition:** Generate all possible orderings of a set of elements — for `n` distinct elements, there are `n!` permutations.

```java
public List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, new ArrayList<>(), new boolean[nums.length], result);
    return result;
}

private void backtrack(int[] nums, List<Integer> current, boolean[] used, List<List<Integer>> result) {
    if (current.size() == nums.length) {
        result.add(new ArrayList<>(current));   // complete permutation found
        return;
    }

    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;   // skip elements already placed in this permutation

        current.add(nums[i]);       // choose
        used[i] = true;
        backtrack(nums, current, used, result);  // explore
        current.remove(current.size() - 1);      // un-choose
        used[i] = false;                          // free this element for other branches
    }
}
```

**Why `used[]` is needed here but NOT in Subsets (a key distinction interviewers probe):** in Subsets, order doesn't matter and you never revisit earlier indices (`start` parameter enforces this). In Permutations, order matters and every element must appear exactly once per permutation, but can be reused across different branches — so you need to track "is this element currently placed in the current path" rather than a simple start index.

**Walkthrough on `[1,2,3]`, first few branches:**

```
[1] → [1,2] → [1,2,3] ✓
    → [1,3] → [1,3,2] ✓
[2] → [2,1] → [2,1,3] ✓
    ...
```

---

### 4. N-Queens

**Definition:** Place `N` queens on an `N×N` chessboard such that no two queens attack each other (no shared row, column, or diagonal) — a classic constraint-satisfaction backtracking problem.

**Key simplification:** since no two queens can share a row, you can place exactly one queen per row — this reduces the problem to choosing a valid column for each row, one row at a time.

```java
public List<List<String>> solveNQueens(int n) {
    List<List<String>> result = new ArrayList<>();
    int[] queens = new int[n];   // queens[row] = column of queen placed in that row
    backtrack(queens, 0, n, result);
    return result;
}

private void backtrack(int[] queens, int row, int n, List<List<String>> result) {
    if (row == n) {
        result.add(buildBoard(queens, n));   // all rows filled — valid solution
        return;
    }

    for (int col = 0; col < n; col++) {
        if (isValid(queens, row, col)) {
            queens[row] = col;              // choose
            backtrack(queens, row + 1, n, result);   // explore next row
            // no explicit "un-choose" needed — queens[row] gets overwritten next iteration
        }
    }
}

private boolean isValid(int[] queens, int row, int col) {
    for (int prevRow = 0; prevRow < row; prevRow++) {
        int prevCol = queens[prevRow];
        if (prevCol == col) return false;                          // same column
        if (Math.abs(prevCol - col) == Math.abs(prevRow - row)) return false;  // same diagonal
    }
    return true;
}

private List<String> buildBoard(int[] queens, int n) {
    List<String> board = new ArrayList<>();
    for (int col : queens) {
        StringBuilder row = new StringBuilder();
        for (int i = 0; i < n; i++) row.append(i == col ? 'Q' : '.');
        board.add(row.toString());
    }
    return board;
}
```

**Why the diagonal check works (`Math.abs(prevCol - col) == Math.abs(prevRow - row)`):** two cells are on the same diagonal exactly when the difference in their row indices equals the difference in their column indices (either direction — hence `abs`). This is the core "constraint check" that makes N-Queens a backtracking problem rather than a simple permutation.

**Why this problem is a great showcase of pruning (worth stating explicitly):** `isValid()` prunes invalid branches immediately, before recursing deeper — you never waste time exploring a full sub-tree that was doomed from an early bad choice. This is backtracking's core efficiency advantage over pure brute force (try every arrangement, check validity only at the end).

---

### 5. Word Search

**Definition:** Given a 2D grid of letters and a word, determine if the word can be constructed by tracing a path of adjacent cells (horizontally/vertically), without reusing the same cell twice.

```java
public boolean exist(char[][] board, String word) {
    int rows = board.length, cols = board[0].length;
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (backtrack(board, word, r, c, 0)) {
                return true;   // found a starting point that works
            }
        }
    }
    return false;
}

private boolean backtrack(char[][] board, String word, int r, int c, int index) {
    if (index == word.length()) return true;   // matched the whole word

    if (r < 0 || r >= board.length || c < 0 || c >= board[0].length 
        || board[r][c] != word.charAt(index)) {
        return false;   // out of bounds or letter mismatch — dead end
    }

    char temp = board[r][c];
    board[r][c] = '#';   // mark as visited (choose) — prevents reusing this cell

    boolean found = backtrack(board, word, r + 1, c, index + 1)
                 || backtrack(board, word, r - 1, c, index + 1)
                 || backtrack(board, word, r, c + 1, index + 1)
                 || backtrack(board, word, r, c - 1, index + 1);

    board[r][c] = temp;   // un-choose — restore the letter for other paths

    return found;
}
```

**Why marking the cell as `'#'` and restoring it is the backtracking pattern here (important to articulate):** this is the "choose/un-choose" step applied to a grid instead of a list — you temporarily mark a cell as used so it isn't reused within the current path, then restore it once you backtrack, so a different path exploring a different direction can still use that cell.

**Why `||` short-circuits efficiently:** if the first direction (`r+1`) already returns `true`, Java's `||` skips evaluating the rest — no wasted exploration once a valid path is found.
