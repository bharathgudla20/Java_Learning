# Longest Subsequence With Non-Zero Bitwise XOR

## Problem

You are given an integer array `nums`.

Return the length of the longest subsequence in `nums` whose bitwise XOR is non-zero. If no such subsequence exists, return `0`.

### Example 1

```text
Input: nums = [1,2,3]
Output: 2
Explanation: [2,3] -> 2 XOR 3 = 1, which is non-zero.
```

### Example 2

```text
Input: nums = [2,3,4]
Output: 3
Explanation: [2,3,4] -> 2 XOR 3 XOR 4 = 5, which is non-zero.
```

---

## Key Observation

The XOR of all elements in the array is either:

- non-zero, or
- zero

### Case 1: Total XOR is non-zero

If the XOR of all elements is non-zero, then the entire array itself is already a valid subsequence with non-zero XOR.

So the answer is simply:

```text
return nums.length
```

### Case 2: Total XOR is zero

Now the whole array XOR is zero, so we must remove one element to make the XOR non-zero.

- If there is at least one non-zero element in the array, remove one such element.
- If all elements are zero, then every subsequence XOR is also zero, so the answer is `0`.

So the answer becomes:

```text
return nums.length - 1
```

if at least one non-zero element exists.

---

## Golden Rule of Multiple XORs

When XORing a vertical column of bits:

- Count how many `1`s appear in that column.
- If the count is even, the result for that bit is `0`.
- If the count is odd, the result for that bit is `1`.

This is the reason the total XOR being zero or non-zero matters.

---

## Java Solution (Optimal)

```java
class Solution {
    public int longestSubsequence(int[] nums) {
        int totalXor = 0;
        int n = nums.length;
        boolean hasNonZero = false;

        for (int x : nums) {
            totalXor ^= x;
            if (x != 0) {
                hasNonZero = true;
            }
        }

        if (totalXor != 0) {
            return n;
        }

        if (hasNonZero) {
            return n - 1;
        }

        return 0;
    }
}
```

---

## Explanation

1. Compute the XOR of the entire array.
2. If it is non-zero, then the whole array itself is the longest valid subsequence.
3. If it is zero:
   - If the array contains any non-zero element, removing one such element makes XOR non-zero.
   - If all elements are zero, no non-zero subsequence exists.

---

## Complexity

- Time: `O(n)`
- Space: `O(1)`

---

## Your DP Idea

You also wrote a recursive DP version:

```java
class Solution {
    int maxsubseq(int i,int n,int[] nums,int xor1,int len,int[] dp)
    {
        if(dp[i]!=0)
        {
            return dp[i];
        }
        if(i==n)
        {
            if(xor1!=0)
            {
                return len;
            }
            else
            {
                return 0;
            }
        }

        int take=maxsubseq(i+1,n,nums,xor1^nums[i],len+1,dp);
        int nottake=maxsubseq(i+1,n,nums,xor1,len,dp);
        return dp[i]=Math.max(take,nottake);
    }
    public int longestSubsequence(int[] nums) {
        int dp[]=new int[nums.length+1];
        return maxsubseq(0,nums.length,nums,0,0,dp);
    }
}
```

### Why this DP is not the best choice

This approach tries all subsequences, so it has exponential time complexity in the worst case.

- Number of subsequences = `2^n`
- For large `n`, this is too slow for LeetCode constraints.

The key insight above is much simpler and much faster.

---

## Final Summary

The efficient rule is:

```text
if totalXor != 0 -> return n
if totalXor == 0 and array has non-zero -> return n - 1
else -> return 0
```

This is the optimal solution for the problem.
