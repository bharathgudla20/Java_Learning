# Phase 7, Topic 7: Dynamic Programming Basics — Memoization, Tabulation, Fibonacci, Knapsack, LCS

DP has a reputation for being the "scariest" DSA topic, but it's really just recursion + smart caching. This topic tends to separate strong candidates from average ones at product companies since it requires recognizing overlapping subproblems, not just implementing a known algorithm.

### Why this matters

Many "hard" interview problems are really just recursive brute force with repeated work — DP is the fix. Once you can spot "this recursion is solving the same subproblem multiple times," the DP solution almost writes itself. Knapsack and LCS are the two template problems that basically every other DP question is a variation of.

---

### 1. The Core Idea — Why DP Exists

**Definition:** Dynamic Programming solves problems by breaking them into overlapping subproblems, solving each subproblem only once, and storing (caching) the result for reuse — trading space for a huge time improvement.

**Two requirements a problem must have for DP to apply (always mention these when asked "how do you know DP applies?"):**

1. **Optimal substructure** — the optimal solution to the problem can be built from optimal solutions to its subproblems.
2. **Overlapping subproblems** — the same subproblems get solved repeatedly in a naive recursive approach.

---

### 2. Fibonacci — the "hello world" of DP

**Naive recursion — exposes the problem:**

```java
public int fib(int n) {
    if (n <= 1) return n;
    return fib(n - 1) + fib(n - 2);
}
```

**Why this is slow — O(2ⁿ):** draw the recursion tree for `fib(5)` — `fib(3)` gets computed **twice** (once from `fib(4)`, once directly), `fib(2)` gets computed **three times**, and so on. The tree branches exponentially, recomputing the same values over and over.

```
                    fib(5)
                /            \
           fib(4)              fib(3)
          /      \             /      \
      fib(3)    fib(2)      fib(2)   fib(1)
      /    \     /   \       /   \
  fib(2) fib(1) fib(1) fib(0) fib(1) fib(0)
```

Notice `fib(3)` appears twice, `fib(2)` appears three times — that's the "overlapping subproblems" waste DP eliminates.

#### Approach 1 — Memoization (Top-Down)

**Definition:** Keep the natural recursive structure, but cache results in a map/array the first time you compute them — subsequent calls with the same input return instantly.

```java
public int fibMemo(int n, int[] memo) {
    if (n <= 1) return n;
    if (memo[n] != -1) return memo[n];   // already computed — return cached value
    memo[n] = fibMemo(n - 1, memo) + fibMemo(n - 2, memo);
    return memo[n];
}
// Call: int[] memo = new int[n+1]; Arrays.fill(memo, -1); fibMemo(n, memo);
```

This reduces the time complexity to **O(n)** — each value from `0` to `n` is computed exactly once.

#### Approach 2 — Tabulation (Bottom-Up)

**Definition:** Build the solution iteratively from the base cases upward, filling a table (array), instead of recursing top-down.

```java
public int fibTab(int n) {
    if (n <= 1) return n;
    int[] dp = new int[n + 1];
    dp[0] = 0;
    dp[1] = 1;
    for (int i = 2; i <= n; i++) {
        dp[i] = dp[i - 1] + dp[i - 2];
    }
    return dp[n];
}
```

**Space-optimized version (a very common interview follow-up — "can you do it in O(1) space?"):**

```java
public int fibOptimized(int n) {
    if (n <= 1) return n;
    int prev2 = 0, prev1 = 1;
    for (int i = 2; i <= n; i++) {
        int curr = prev1 + prev2;
        prev2 = prev1;
        prev1 = curr;
    }
    return prev1;
}
```

**Why this works:** Fibonacci only ever needs the last 2 values, not the whole table — so you can discard everything else. This "rolling variable" optimization applies to lots of DP problems where each state only depends on a small, fixed window of previous states.

**Memoization vs Tabulation — a classic comparison question:**

| Concept | Memoization (Top-Down) | Tabulation (Bottom-Up) |
|---------|------------------------|------------------------|
| Direction | Starts from the original problem, recurses down | Starts from base cases, builds up |
| Implementation | Recursion + cache | Iteration + array |
| Stack usage | Uses call stack (risk of stack overflow for large n) | No recursion, no stack overflow risk |
| Computes only what's needed? | Yes — only subproblems actually reached | No — typically computes all subproblems up to n |

---

### 3. 0/1 Knapsack — the template for "choose or don't choose" problems

**Definition:** Given items with weights and values, and a knapsack with a maximum weight capacity, find the maximum value achievable without exceeding the capacity — each item can be used at most once (hence "0/1" — take it or leave it).

**Naive recursion (exposes the "choice" structure):**

```java
public int knapsackRecursive(int[] weights, int[] values, int capacity, int n) {
    if (n == 0 || capacity == 0) return 0;   // base case: no items or no space left

    if (weights[n - 1] > capacity) {
        // Can't include this item — skip it
        return knapsackRecursive(weights, values, capacity, n - 1);
    }
    
    // Choice: include the item, OR exclude it — take the better option
    int include = values[n - 1] + knapsackRecursive(weights, values, capacity - weights[n - 1], n - 1);
    int exclude = knapsackRecursive(weights, values, capacity, n - 1);
    return Math.max(include, exclude);
}
```

**The "state" to identify for DP (a crucial interview skill):** the result depends on which item you're considering (`n`) and how much capacity remains (`capacity`) — those two variables define the subproblem, so they become the dimensions of your DP table.

**Tabulation — 2D DP table:**

```java
public int knapsack(int[] weights, int[] values, int capacity) {
    int n = weights.length;
    int[][] dp = new int[n + 1][capacity + 1];   // dp[i][w] = max value using first i items, capacity w

    for (int i = 1; i <= n; i++) {
        for (int w = 0; w <= capacity; w++) {
            if (weights[i - 1] > w) {
                dp[i][w] = dp[i - 1][w];   // can't fit this item — same as without it
            } else {
                dp[i][w] = Math.max(
                    dp[i - 1][w],                                      // exclude item i
                    values[i - 1] + dp[i - 1][w - weights[i - 1]]      // include item i
                );
            }
        }
    }
    return dp[n][capacity];
}
```

**Walkthrough (small example):** `weights = [1, 3, 4]`, `values = [15, 20, 30]`, `capacity = 4`

- Best combo: item 1 (w=1,v=15) + item 2 (w=3,v=20) = weight 4, value 35. Or item 3 alone (w=4, v=30).
- DP correctly finds **35** as the max — better than greedily picking the highest-value item alone.

**Why greedy fails here (worth mentioning to show you understand why DP is needed):** picking the highest value-to-weight ratio item first doesn't always lead to the optimal combination — unlike the Fractional Knapsack problem (where greedy does work because you can take partial items), 0/1 Knapsack requires exploring combinations, which is exactly what DP's "include vs exclude" table captures.

---

### 4. LCS — Longest Common Subsequence

**Definition:** Given two strings, find the length of their longest subsequence common to both — a subsequence doesn't need to be contiguous, just in the same relative order.

**Example:** `"abcde"` and `"ace"` → LCS is `"ace"`, length `3`.

**The recursive insight (state = two pointers, one per string):**

```java
public int lcsRecursive(String s1, String s2, int i, int j) {
    if (i == s1.length() || j == s2.length()) return 0;   // base case: ran out of characters

    if (s1.charAt(i) == s2.charAt(j)) {
        return 1 + lcsRecursive(s1, s2, i + 1, j + 1);   // characters match — take both, move both forward
    } else {
        // characters don't match — try skipping one character from EITHER string, take the best
        return Math.max(
            lcsRecursive(s1, s2, i + 1, j),
            lcsRecursive(s1, s2, i, j + 1)
        );
    }
}
```

**Tabulation — 2D DP table:**

```java
public int longestCommonSubsequence(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[][] dp = new int[m + 1][n + 1];   // dp[i][j] = LCS length of s1[0..i) and s2[0..j)

    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                dp[i][j] = 1 + dp[i - 1][j - 1];         // match — extend diagonal
            } else {
                dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);  // no match — best of skipping either char
            }
        }
    }
    return dp[m][n];
}
```

**Walkthrough on `"abcde"` vs `"ace"`:** the table fills such that whenever characters match, you extend the diagonal value by 1; when they don't, you carry forward the best from the left or top cell. Final answer sits in `dp[m][n]` = **3** (`"ace"`).

**Why LCS is a "template" problem (worth stating):** many other DP problems are direct variations — Longest Common Substring (contiguous version), Edit Distance, Shortest Common Supersequence — all use the same 2D-table-with-two-pointers structure.
