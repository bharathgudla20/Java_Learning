# Number of Substrings Containing All Three Characters

LeetCode 1358 - Medium

## Problem Statement

Given a string `s` consisting only of characters `'a'`, `'b'`, and `'c'`, return the number of substrings that contain at least one occurrence of all three characters.

### Example 1

Input: `s = "abcabc"`

Output: `10`

### Example 2

Input: `s = "aaacb"`

Output: `3`

### Example 3

Input: `s = "abc"`

Output: `1`

---

## Approach

A substring is valid if it contains all three characters: `'a'`, `'b'`, and `'c'`.

Instead of checking every substring separately, we keep track of the latest index seen for each character while scanning from left to right.

For each position `i`:
- update the latest index of `s[i]`
- if all three characters have been seen, then the current position can be the ending point of many valid substrings
- the number of valid substrings ending at `i` is `min(lastA, lastB, lastC) + 1`

This gives an efficient linear-time solution.

---

## Java Solution

```java
class Solution {
    public int numberOfSubstrings(String s) {
        int lastA = -1;
        int lastB = -1;
        int lastC = -1;
        int total = 0;

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            if (ch == 'a') {
                lastA = i;
            } else if (ch == 'b') {
                lastB = i;
            } else if (ch == 'c') {
                lastC = i;
            }

            if (lastA != -1 && lastB != -1 && lastC != -1) {
                int minIndex = Math.min(lastA, Math.min(lastB, lastC));
                total += minIndex + 1;
            }
        }

        return total;
    }
}
```

---

## Why This Works

At each index `i`, the last seen positions of `'a'`, `'b'`, and `'c'` define the shortest possible valid substring that ends at `i`.

If the three positions are:
- `lastA`
- `lastB`
- `lastC`

then any starting index from `0` up to `min(lastA, lastB, lastC)` will produce a valid substring ending at `i`.

So the count contributed at this step is:

$$
\text{valid substrings ending at } i = \min(lastA, lastB, lastC) + 1
$$

Summing this over the full string gives the total answer.

---

## Complexity Analysis

- Time Complexity: `O(n)`
- Space Complexity: `O(1)`

---

## Example Walkthrough

For `s = "aaacb"`:

- When we reach `'a'`, `'b'`, and `'c'`, we can form valid substrings ending at that position.
- The algorithm counts all such substrings in one pass.

This produces the answer `3`.
