# Largest Almost Missing Integer

You are given an integer array `nums` and an integer `k`.

An integer `x` is almost missing from `nums` if `x` appears in exactly one subarray of size `k` within `nums`.

Return the largest almost missing integer from `nums`. If no such integer exists, return `-1`.

A subarray is a contiguous sequence of elements within an array.

## Example 1

**Input:** `nums = [3,9,2,1,7], k = 3`

**Output:** `7`

**Explanation:**

- `1` appears in 2 subarrays of size 3: `[9, 2, 1]` and `[2, 1, 7]`.
- `2` appears in 3 subarrays of size 3: `[3, 9, 2]`, `[9, 2, 1]`, `[2, 1, 7]`.
- `3` appears in 1 subarray of size 3: `[3, 9, 2]`.
- `7` appears in 1 subarray of size 3: `[2, 1, 7]`.
- `9` appears in 2 subarrays of size 3: `[3, 9, 2]`, `[9, 2, 1]`.

We return `7` since it is the largest integer that appears in exactly one subarray of size `k`.

## Example 2

**Input:** `nums = [3,9,7,2,1,7], k = 4`

**Output:** `3`

**Explanation:**

- `1` appears in 2 subarrays of size 4: `[9, 7, 2, 1]`, `[7, 2, 1, 7]`.
- `2` appears in 3 subarrays of size 4: `[3, 9, 7, 2]`, `[9, 7, 2, 1]`, `[7, 2, 1, 7]`.
- `3` appears in 1 subarray of size 4: `[3, 9, 7, 2]`.
- `7` appears in 3 subarrays of size 4: `[3, 9, 7, 2]`, `[9, 7, 2, 1]`, `[7, 2, 1, 7]`.
- `9` appears in 2 subarrays of size 4: `[3, 9, 7, 2]`, `[9, 7, 2, 1]`.

We return `3` since it is the largest and only integer that appears in exactly one subarray of size `k`.

## Example 3

**Input:** `nums = [0,0], k = 1`

**Output:** `-1`

**Explanation:**

There is no integer that appears in only one subarray of size 1.

---

## Key Observation

The main idea is to focus on how many subarrays of length `k` contain each position.

- If `k == n`, there is only one subarray, which is the entire array. Hence, every element appears in exactly one subarray, so we can simply return the maximum element.
- If `k == 1`, every subarray contains exactly one element. Therefore, an element can be the answer only if its value occurs exactly once. We choose the largest such element.
- If `1 < k < n`:
  - The first element `nums[0]` belongs only to the first subarray.
  - The last element `nums[n - 1]` belongs only to the last subarray.
  - Every middle element belongs to more than one subarray.

Therefore, only the first and last elements can possibly satisfy the condition of appearing in exactly one subarray. We use a frequency map to check whether their values occur exactly once.

---

## Java Solution

```java
import java.util.*;

class Solution {
    public int largestInteger(int[] nums, int k) {
        HashMap<Integer, Integer> mp = new HashMap<>();
        for (int x : nums) {
            mp.put(x, mp.getOrDefault(x, 0) + 1);
        }

        int n = nums.length - 1;

        if (k == 1) {
            int large = -1;
            for (int x : mp.keySet()) {
                if (mp.get(x) == 1) {
                    large = Math.max(large, x);
                }
            }
            return large;
        }

        if (k == n + 1) {
            int[] arr = nums.clone();
            Arrays.sort(arr);
            return arr[n];
        }

        if (mp.get(nums[0]) > 1 && mp.get(nums[n]) > 1) {
            return -1;
        }

        if (nums[n] > nums[0]) {
            if (mp.get(nums[n]) > 1) {
                return nums[0];
            }
            return nums[n];
        } else {
            if (mp.get(nums[0]) > 1) {
                return nums[n];
            }
            return nums[0];
        }
    }
}
```

---

## Why this works

- For `k == 1`, every element is in exactly one subarray, so the answer is the largest value whose frequency is exactly `1`.
- For `k == n`, there is only one subarray: the entire array. So the answer is simply the maximum element in the array.
- For `1 < k < n`, only the first and last elements can be present in exactly one subarray. So we only need to compare the values at both ends and check whether they appear more than once in the full array.

This reduces the problem to checking the frequency of the first and last elements, which makes the logic efficient and simple.
