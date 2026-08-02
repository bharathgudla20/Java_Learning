# Predict the Winner

## Intuition

Instead of calculating the individual scores of both players, we calculate the maximum score difference the current player can achieve over the opponent.

Let `solve(i, j)` represent the maximum score difference that the current player can obtain from the subarray `nums[i...j]`.

At every turn, the current player has two choices:

- Pick the left element `nums[i]`
- Pick the right element `nums[j]`

After making a choice, the opponent becomes the current player for the remaining subarray. Since the opponent also plays optimally, we subtract the opponent’s best possible score difference from the value we pick.

If the final score difference is greater than or equal to `0`, Player 1 can guarantee at least a tie, which counts as a win.

## Java Solution

```java
class Solution {
    int maxDiff(int i, int j, int[] nums, int[][] dp) {
        if (dp[i][j] != -1) {
            return dp[i][j];
        }
        if (i == j) {
            return dp[i][j] = nums[i];
        }

        return dp[i][j] = Math.max(
            nums[i] - maxDiff(i + 1, j, nums, dp),
            nums[j] - maxDiff(i, j - 1, nums, dp)
        );
    }

    public boolean predictTheWinner(int[] nums) {
        int n = nums.length;
        if ((n & 1) == 0) {
            return true;
        }

        int[][] dp = new int[n][n];
        for (int[] r : dp) {
            Arrays.fill(r, -1);
        }

        return maxDiff(0, n - 1, nums, dp) >= 0;
    }
}
```

## Complexity

- Time Complexity: `O(n^2)`
- Space Complexity: `O(n^2)`
