# 115. Distinct Subsequences

## Problem Statement

Given two strings `s` and `t`, return the number of distinct subsequences of `s` which equals `t`.

The test cases are generated so that the answer fits in a 32-bit signed integer.

## Examples

**Example 1:**

```text
Input: s = "rabbbit", t = "rabbit"
Output: 3
```

**Example 2:**

```text
Input: s = "babgbag", t = "bag"
Output: 5
```

## Approach

Use top-down dynamic programming with memoization.

- `func(i, j)` represents the number of ways to form `t[j...]` from `s[i...]`.
- If `j == t.length()`, the complete target has been formed, so return `1`.
- If `i == s.length()` before forming the target, return `0`.
- When `s[i] == t[j]`, either use the current character or skip it.
- When the characters differ, skip the current character in `s`.

## Java Solution

```java
import java.util.Arrays;

class Solution {
    public int func(int i, int j, int n1, int n2, String s, String t, int[][] dp) {
        if (j == n2) {
            return 1;
        }
        if (i == n1) {
            return 0;
        }
        if (dp[i][j] != -1) {
            return dp[i][j];
        }

        if (s.charAt(i) == t.charAt(j)) {
            dp[i][j] = func(i + 1, j + 1, n1, n2, s, t, dp)
                    + func(i + 1, j, n1, n2, s, t, dp);
        } else {
            dp[i][j] = func(i + 1, j, n1, n2, s, t, dp);
        }

        return dp[i][j];
    }

    public int numDistinct(String s, String t) {
        int[][] dp = new int[s.length()][t.length()];
        for (int[] row : dp) {
            Arrays.fill(row, -1);
        }

        return func(0, 0, s.length(), t.length(), s, t, dp);
    }
}
```

## Complexity

- Time Complexity: `O(n * m)`
- Space Complexity: `O(n * m)`

where `n = s.length()` and `m = t.length()`.
