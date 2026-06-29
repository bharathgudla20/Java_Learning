# 1967. Number of Strings That Appear as Substrings in Word

## Problem Statement

Given an array of strings `patterns` and a string `word`, return the number of strings in `patterns` that appear as a substring in `word`.

A substring is a contiguous sequence of characters within a string.

### Example

```text
Input: patterns = ["a", "abc", "bc", "d"], word = "abc"
Output: 3
```

---

## Approach

For each pattern, check whether it exists inside `word` using Java's `contains` method.
If it does, increase the answer by 1.

This is simple and efficient for this problem because we only need to know whether each pattern is present.

---

## Java Solution

```java
class Solution {
    public int numOfStrings(String[] patterns, String word) {
        int ans = 0;

        for (String pattern : patterns) {
            if (word.contains(pattern)) {
                ans++;
            }
        }

        return ans;
    }
}
```

---

## Complexity

- Time Complexity: O(n * m) in the worst case, where `n` is the number of patterns and `m` is the length of `word`
- Space Complexity: O(1)
