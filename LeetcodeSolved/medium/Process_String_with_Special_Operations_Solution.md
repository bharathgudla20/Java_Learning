# Process String with Special Operations

LeetCode Medium

## Problem

You are given a string `s` consisting of lowercase English letters and the special characters: `*`, `#`, and `%`.

Build a new string `result` by processing `s` according to the following rules from left to right:

- If the letter is a lowercase English letter append it to `result`.
- A `*` removes the last character from `result`, if it exists.
- A `#` duplicates the current `result` and appends it to itself.
- A `%` reverses the current `result`.

Return the final string `result` after processing all characters in `s`.

### Example

Input: `s = "a#b%*"

Output: `"ba"`

Explanation:

- `a` -> "a"
- `#` -> "aa"
- `b` -> "aab"
- `%` -> "baa"
- `*` -> "ba"

## Java Solution

```java
class Solution {
    public String processStr(String s) {
        StringBuilder ans = new StringBuilder();

        for (char ch : s.toCharArray()) {
            if (ch == '*') {
                if (ans.length() > 0) {
                    ans.deleteCharAt(ans.length() - 1);
                }
            } else if (ch == '#') {
                ans.append(ans);
            } else if (ch == '%') {
                ans.reverse();
            } else {
                ans.append(ch);
            }
        }

        return ans.toString();
    }
}
```

## Notes

- Use `StringBuilder` for efficient in-place string updates.
- `deleteCharAt` safely removes the last character when the result is non-empty.
- `append(ans)` duplicates the current content.
- `reverse()` reverses the accumulated result immediately.
