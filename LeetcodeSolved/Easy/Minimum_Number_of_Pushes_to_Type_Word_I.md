# Minimum Number of Pushes to Type Word I

You are given a string `word` containing distinct lowercase English letters.

Telephone keypads have keys mapped with distinct collections of lowercase English letters. For example, key `2` can be mapped with `"a", "b", "c"`, where:
- one push types `"a"`
- two pushes types `"b"`
- three pushes types `"c"`

It is allowed to remap the keys numbered `2` to `9` to distinct collections of letters. Each letter must be assigned to exactly one key.

Your task is to find the minimum number of pushes required to type the entire string `word`.

## Example

**Input:** `word = "xycdefghij"`

**Output:** `12`

## Intuition

The first 8 distinct letters can be assigned to different keys with cost `1` each.

After that:
- the next 8 letters cost `2` pushes each
- the next 8 letters cost `3` pushes each
- any letters beyond that cost `4` pushes each

So the answer depends only on the length of the word.

## Java Solution

```java
class Solution {
    public int minimumPushes(String word) {
        int n = word.length();

        if (n <= 8) {
            return n;
        }
        if (n <= 16) {
            return 8 + (n - 8) * 2;
        }
        if (n <= 24) {
            return 8 + 8 * 2 + (n - 16) * 3;
        }

        return 8 + 8 * 2 + 8 * 3 + (n - 24) * 4;
    }
}
```

## Explanation

- If the word length is at most `8`, each letter can be placed on a separate key, so the cost is just `n`.
- If the length is between `9` and `16`, the first 8 letters cost `1` push each, and the remaining letters cost `2` pushes each.
- If the length is between `17` and `24`, the cost pattern becomes `1`, `1`, `1`, ... for the first 8 letters, then `2` for the next 8, then `3` for the rest.
- For longer words, the cost increases to `4` pushes per letter after 24 characters.

This gives the minimum possible number of pushes after optimal remapping.
