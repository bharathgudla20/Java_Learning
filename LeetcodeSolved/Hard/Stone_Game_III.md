# Stone Game III

## Problem
Alice and Bob continue their games with piles of stones. There are several stones arranged in a row, and each stone has an associated value which is an integer given in the array `stoneValue`.

Alice and Bob take turns, with Alice starting first. On each player's turn, that player can take `1`, `2`, or `3` stones from the first remaining stones in the row.

The score of each player is the sum of the values of the stones taken. The score of each player is `0` initially.

The objective of the game is to end with the highest score, and the winner is the player with the highest score and there could be a tie. The game continues until all the stones have been taken.

Assume Alice and Bob play optimally.

Return `"Alice"` if Alice will win, `"Bob"` if Bob will win, or `"Tie"` if they will end the game with the same score.

## Java Solution

```java
class Solution {
    public int func(int i, int n, int[] dp, int[] nums) {
        if (i >= n) {
            return 0;
        }
        if (dp[i] != -1)
            return dp[i];

        int a = nums[i] - func(i + 1, n, dp, nums);
        int b = Integer.MIN_VALUE, c = Integer.MIN_VALUE;
        if (i + 1 < n)
            b = (nums[i] + nums[i + 1]) - func(i + 2, n, dp, nums);
        if (i + 2 < n)
            c = (nums[i] + nums[i + 1] + nums[i + 2]) - func(i + 3, n, dp, nums);

        return dp[i] = Math.max(a, Math.max(b, c));
    }

    public String stoneGameIII(int[] stoneValue) {
        int n = stoneValue.length;
        int[] dp = new int[n];
        Arrays.fill(dp, -1);
        int tot = func(0, n, dp, stoneValue);

        if (tot > 0) {
            return "Alice";
        }
        if (tot == 0) {
            return "Tie";
        }
        return "Bob";
    }
}
```

## Explanation

- `func(i, n, dp, nums)` returns the maximum score difference the current player can achieve over the opponent starting from index `i`.
- For each choice of taking `1`, `2`, or `3` stones, compute the sum of values taken and subtract the result of the opponent's best response from the next index.
- `dp[i]` caches intermediate results to avoid recomputation.
- The final winner is determined from `tot`:
  - `tot > 0`: Alice wins
  - `tot == 0`: Tie
  - `tot < 0`: Bob wins
