# Maximum Length Substring With Two Occurrences

Given a string `s`, find the maximum length of a substring that contains at most two occurrences of each character.

## Example 1

**Input:** `s = "bcbbbcba"`

**Output:** `4`

**Explanation:**
The longest substring with at most two occurrences of each character is `"bcbb"` (or `"bbbc"` depending on the starting index), and its length is `4`.

## Example 2

**Input:** `s = "aaaa"`

**Output:** `2`

**Explanation:**
The longest substring with at most two occurrences of each character is `"aa"` and its length is `2`.

---

## Intuition

We try every starting index `i` and expand the substring from `i` to the right.

For each substring:
- Count how many times each character appears.
- If any character appears more than 2 times, stop expanding.
- Keep track of the longest valid substring length.

This is a brute-force approach with a frequency map, which is easy to understand and works well for the constraints of this problem.

---

## Java Solution

```java
import java.util.HashMap;

class Solution {
    public int maximumLengthSubstring(String s) {
        int maxlen = 0;

        for (int i = 0; i < s.length(); i++) {
            HashMap<Character, Integer> mp = new HashMap<>();

            for (int j = i; j < s.length(); j++) {
                char ch = s.charAt(j);
                mp.put(ch, mp.getOrDefault(ch, 0) + 1);

                if (mp.get(ch) > 2) {
                    break;
                }

                maxlen = Math.max(maxlen, j - i + 1);
            }
        }

        return maxlen;
    }
}
```

---

## Explanation

- `i` represents the start of the substring.
- `j` moves forward from `i` and counts characters in `mp`.
- If a character count exceeds `2`, we break out of the inner loop because that substring is no longer valid.
- `maxlen` stores the longest valid substring found so far.

### Time Complexity

- Outer loop: `O(n)`
- Inner loop: `O(n)` in the worst case
- Total: `O(n^2)`

### Space Complexity

- `O(k)`, where `k` is the number of distinct characters in the current substring

---

---

## Optimized Approach: Sliding Window

### Approach

- Expand the window by incrementing `j`, adding `s[j]` to a frequency map.
- If `s[j]`'s count exceeds `2`, shrink from the left until it's valid again.
- Track the max window size `(j - i + 1)` after every expansion.

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    public int maximumLengthSubstring(String s) {
        Map<Character, Integer> count = new HashMap<>();
        int i = 0, res = 0;

        for (int j = 0; j < s.length(); j++) {
            char c = s.charAt(j);
            count.put(c, count.getOrDefault(c, 0) + 1);

            while (count.get(c) > 2) {
                char left = s.charAt(i);
                count.put(left, count.get(left) - 1);
                i++;
            }

            res = Math.max(res, j - i + 1);
        }

        return res;
    }
}
```

### Why this works

The sliding window keeps a valid substring in the range `[i, j]` at all times.
When the current character appears more than twice, we move `i` forward until the window becomes valid again.
This ensures we always check the longest valid substring without reprocessing the whole string repeatedly.

### Time Complexity

- Each character is added once and removed once.
- Total: `O(n)`

### Space Complexity

- `O(k)`, where `k` is the number of distinct characters in the string.

---

## Final Note

The brute-force method is easy to understand, while the sliding-window method is the optimal solution for this problem. Both approaches correctly find the maximum valid substring length.
