# Count Subarrays With Majority Element I

## Problem Statement

You are given an integer array `nums` and an integer `target`.

Return the number of **subarrays** of `nums` in which `target` is the **majority element**.

The majority element of a subarray is the element that appears **strictly more than half** of the times in that subarray.

### Example

Input: `nums = [1,2,2,3]`, `target = 2`

Output: `5`

Valid subarrays with `target = 2` as the majority element:
- `nums[1..1] = [2]`
- `nums[2..2] = [2]`
- `nums[1..2] = [2,2]`
- `nums[0..2] = [1,2,2]`
- `nums[1..3] = [2,2,3]`

---

## Approach

### Key idea

Transform the array into a prefix-sum array where:
- `+1` for elements equal to `target`
- `-1` for all other elements

A subarray has `target` as the majority element if and only if its transformed sum is greater than `0`.

Then the problem reduces to counting pairs of prefix sums `(i, j)` with `i < j` and `prefix[j] > prefix[i]`.

### Efficient solution

Use coordinate compression plus a Fenwick Tree (Binary Indexed Tree) to count the number of earlier prefix sums that are strictly smaller than the current prefix sum.

---

## Java Implementation

```java
import java.util.Arrays;

class Solution {
    public long countMajoritySubarrays(int[] nums, int target) {
        int n = nums.length;
        int[] prefix = new int[n + 1];
        int cur = 0;
        prefix[0] = 0;

        for (int i = 0; i < n; i++) {
            cur += nums[i] == target ? 1 : -1;
            prefix[i + 1] = cur;
        }

        int[] all = Arrays.copyOf(prefix, n + 1);
        Arrays.sort(all);

        int uniqueCount = 1;
        for (int i = 1; i < all.length; i++) {
            if (all[i] != all[i - 1]) {
                uniqueCount++;
            }
        }

        int[] values = new int[uniqueCount];
        values[0] = all[0];
        int pos = 1;
        for (int i = 1; i < all.length; i++) {
            if (all[i] != all[i - 1]) {
                values[pos++] = all[i];
            }
        }

        int[] bit = new int[uniqueCount + 1];
        long ans = 0;

        for (int p : prefix) {
            int rank = Arrays.binarySearch(values, p) + 1;
            ans += query(bit, rank - 1);
            update(bit, rank, 1);
        }

        return ans;
    }

    private int query(int[] bit, int idx) {
        int sum = 0;
        while (idx > 0) {
            sum += bit[idx];
            idx -= idx & -idx;
        }
        return sum;
    }

    private void update(int[] bit, int idx, int value) {
        while (idx < bit.length) {
            bit[idx] += value;
            idx += idx & -idx;
        }
    }
}
```

---

## Complexity

- Time: `O(n log n)`
- Space: `O(n)`

---

## Notes

- The brute-force solution is correct but may be too slow for large inputs because it checks all subarrays.
- Converting the problem to prefix sums and counting smaller previous prefix values is the efficient way to solve this problem.
- Coordinate compression ensures the Fenwick Tree works with compact indices.
