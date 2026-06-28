# Find the Maximum Number of Elements in Subset (LeetCode #3020)
**Difficulty:** Medium

## Problem Statement
You are given an array of **positive** integers `nums`.

You need to select a subset of `nums` which satisfies the following condition:

- You can place the selected elements in a **0-indexed** array such that it follows the pattern: `[x, x², x⁴, ..., xᵏ/², xᵏ, xᵏ/², ..., x⁴, x², x]` (**Note** that `k` can be any **non-negative** power of `2`).
  - For example, `[2, 4, 16, 4, 2]` and `[3, 9, 3]` follow the pattern while `[2, 4, 8, 4, 2]` does not.

Return the **maximum** number of elements in a subset that satisfies these conditions.

---

## Examples

### Example 1:
```
Input: nums = [5,4,1,2,2]
Output: 3
Explanation: We can select the subset {4,2,2}, which can be placed in the array as 
[2,4,2] which follows the pattern and 2² == 4. Hence the answer is 3.
```

### Example 2:
```
Input: nums = [1,3,2,4]
Output: 1
Explanation: We can select the subset {1}, which can be placed in the array as [1] 
which follows the pattern. Hence the answer is 1. Note that we could have also 
selected the subsets {2}, {3}, or {4}.
```

---

## Intuition

The required subset must be arranged in the form:

$$[x, x^2, x^4, \ldots, x^k, \ldots, x^4, x^2, x]$$

This is a palindrome where every element except the middle one appears exactly twice. Therefore:

- Every intermediate value in the power chain must have a frequency of **at least 2** (one for the left half, one for the right half).
- Starting from each unique number (except 1), we repeatedly square the current value and continue the chain as long as its frequency is at least 2.
- Each such value contributes **two elements** to the answer.

**Special case - The number 1:**
- Since $1^2 = 1$, it always remains the same value.
- We can use all occurrences if the frequency is odd, or frequency - 1 if even.

---

## Approach

1. **Count frequencies:** Use a HashMap to store the frequency of each number.

2. **Handle the special case of 1:**
   - If its frequency is odd, all occurrences can be used.
   - If even, use `frequency - 1` occurrences.

3. **For every other unique number:**
   - Start from the number and keep squaring it.
   - While the current value exists with frequency ≥ 2:
     - Add 2 to the length (one for each side).
     - Move to the next value: `temp = temp * temp`.
   - After the loop:
     - If the current value exists exactly once, add 1 (it becomes the center).
     - Otherwise, subtract 1 (the last pair cannot be completed).

4. **Track the maximum:** Keep track of the longest valid chain found.

---

## Solution

```java
class Solution {
    public int maximumLength(int[] nums) {
        HashMap<Long, Integer> map = new HashMap<>();

        // Count frequency of each number
        for (int i = 0; i < nums.length; i++) {
            long val = nums[i];
            map.put(val, map.getOrDefault(val, 0) + 1);
        }

        int ans = 1;

        // Handle special case for 1
        if (map.containsKey((long)1)) {
            int f = map.get((long)1);
            if (f % 2 == 0) {
                ans = Math.max(ans, f - 1);
            } else {
                ans = Math.max(ans, f);
            }
        }

        ArrayList<Long> keys = new ArrayList<>(map.keySet());

        // Check every starting number (except 1)
        for (int i = 0; i < keys.size(); i++) {
            long cur = keys.get(i);

            if (cur == (long)1) {
                continue;
            }

            long temp = cur;
            int len = 0;

            // Build the chain by squaring
            while (true) {
                if (!map.containsKey(temp) || map.get(temp) < 2) {
                    break;
                }

                len += 2; // One for left, one for right
                temp = temp * temp;
            }

            // Check if current value can be used as center
            if (map.containsKey(temp) && map.get(temp) == 1) {
                len++;
            } else {
                len--;
            }

            ans = Math.max(ans, len);
        }

        return ans;
    }
}
```

---

## Complexity Analysis

- **Time Complexity:** $O(n + m \log \log V)$
  - $n$ = length of `nums` (for counting frequencies)
  - $m$ = number of unique values
  - $\log \log V$ = maximum depth of squaring chain (since numbers grow exponentially)
  - For typical constraints, this is effectively $O(n)$.

- **Space Complexity:** $O(m)$
  - HashMap to store frequencies of up to $m$ unique values.

---

## Key Insights

1. **Palindrome Structure:** The subset must form a symmetric pattern where each element (except middle) appears exactly twice.

2. **Exponential Growth:** Squaring numbers causes rapid growth, limiting chain depth to approximately $\log \log(\max \text{ value})$.

3. **Special Case Handling:** The number 1 is unique because $1^2 = 1$, breaking the typical squaring pattern.

4. **Greedy Selection:** For each starting number, greedily build the longest chain possible.

---

## Walkthrough - Example 1

**Input:** `nums = [5,4,1,2,2]`

1. **Frequency map:** `{5:1, 4:1, 1:1, 2:2}`
2. **Handle 1:** Frequency is 1 (odd), so `ans = 1`
3. **Check 5:** `temp=5`, `map.get(5)=1 < 2`, exit loop, `len=0` then `len--=-1`, `ans=1`
4. **Check 4:** `temp=4`, `map.get(4)=1 < 2`, exit loop, `len=0` then `len--=-1`, `ans=1`
5. **Check 2:** 
   - `temp=2`, `map.get(2)=2 >= 2`, so `len=2`, `temp=4`
   - `map.get(4)=1 < 2`, exit loop
   - `map.get(4)` doesn't exist or is 1, so `len=2+1=3`
   - `ans=3`

**Output:** `3` ✓

---

## Walkthrough - Example 2

**Input:** `nums = [1,3,2,4]`

1. **Frequency map:** `{1:1, 3:1, 2:1, 4:1}`
2. **Handle 1:** Frequency is 1 (odd), so `ans = 1`
3. **Check 3, 2, 4:** All have frequency 1, so they can't form chains, `ans = 1`

**Output:** `1` ✓
