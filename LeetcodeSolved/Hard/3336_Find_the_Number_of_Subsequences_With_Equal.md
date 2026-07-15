# 3336. Find the Number of Subsequences With Equal GCD

## Problem Statement

Given an integer array `nums`. Your task is to find the number of pairs of non-empty subsequences `(seq1, seq2)` of `nums` that satisfy the following conditions:

- The subsequences `seq1` and `seq2` are disjoint, meaning no index of `nums` is common between them.
- The GCD of the elements of `seq1` is equal to the GCD of the elements of `seq2`.

Return the total number of such pairs. Since the answer may be very large, return it modulo `10^9 + 7`.

## Examples

**Example 1:**
```
Input: nums = [1,2,3,4]
Output: 10
```

**Example 2:**
```
Input: nums = [10,20,30]
Output: 2
```

**Example 3:**
```
Input: nums = [1,1,1,1]
Output: 50
```

## Approach

For every element, we have three options:
- add it to `seq1`
- add it to `seq2`
- ignore it

We only need the current GCD values of both subsequences, so the DP state is `(idx, g1, g2)` where:
- `idx` is the current index in `nums`
- `g1` is the current GCD of `seq1`
- `g2` is the current GCD of `seq2`

Transitions:
- ignore `nums[idx]`
- include `nums[idx]` in `seq1` and update `g1`
- include `nums[idx]` in `seq2` and update `g2`

Base case:
- when all elements are processed, return `1` if `g1 > 0 && g1 == g2`, otherwise `0`.

This can be memoized top-down, then converted to bottom-up DP. The dp values only depend on the next index, so the solution can be space optimized.

## Java Solution

```java
import java.util.Arrays;

class Solution {
    private static final int MOD = 1_000_000_007;
    private int n;
    private int[][][] dp;

    private int solve(int idx, int g1, int g2, int[] nums) {
        if (idx == n) {
            return (g1 != 0 && g1 == g2) ? 1 : 0;
        }

        if (dp[idx][g1][g2] != -1) {
            return dp[idx][g1][g2];
        }

        long ans = 0;

        // Ignore current element
        ans = solve(idx + 1, g1, g2, nums);

        // Put in seq1
        int ng1 = (g1 == 0) ? nums[idx] : gcd(g1, nums[idx]);
        ans = (ans + solve(idx + 1, ng1, g2, nums)) % MOD;

        // Put in seq2
        int ng2 = (g2 == 0) ? nums[idx] : gcd(g2, nums[idx]);
        ans = (ans + solve(idx + 1, g1, ng2, nums)) % MOD;

        return dp[idx][g1][g2] = (int) ans;
    }

    public int subsequencePairCount(int[] nums) {
        n = nums.length;
        dp = new int[n + 1][201][201];

        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= 200; j++) {
                Arrays.fill(dp[i][j], -1);
            }
        }

        return solve(0, 0, 0, nums);
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```
