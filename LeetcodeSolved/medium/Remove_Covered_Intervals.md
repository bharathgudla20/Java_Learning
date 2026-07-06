# 1288. Remove Covered Intervals

## Problem Statement

Given an array of intervals where each interval is `[l, r)`, remove all intervals that are covered by another interval.

An interval `[a, b)` is covered by `[c, d)` if:

- `c <= a`
- `b <= d`

Return the number of remaining intervals.

---

## Example

### Example 1

Input:
```java
intervals = [[1,4],[3,6],[2,8]]
```

Output:
```java
2
```

Explanation:
- `[3,6)` is covered by `[2,8)`
- So it is removed

### Example 2

Input:
```java
intervals = [[1,4],[2,3]]
```

Output:
```java
1
```

---

## Key Idea

Sort the intervals by:

1. Starting point increasing
2. Ending point decreasing

After sorting, while scanning:
- If the current interval ends after the previous maximum right endpoint, it remains
- Otherwise, it is covered and should be removed

---

## Java Solution

```java
class Solution {
    public int removeCoveredIntervals(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> a[0] != b[0] ? a[0] - b[0] : b[1] - a[1]);

        int res = 0, r = 0;

        for (int[] x : intervals) {
            if (x[1] > r) {
                r = x[1];
                res++;
            }
        }

        return res;
    }
}
```

---

## Explanation

After sorting:
- Intervals with smaller start come first
- If two intervals start same, the longer one comes first

Then we keep track of the maximum right endpoint seen so far.

If the current interval ends after this maximum, it is not covered and we count it.

Otherwise, it is covered by a previous interval.

---

## Complexity

- Time Complexity: `O(n log n)`
- Space Complexity: `O(1)` extra space

Where `n` is the number of intervals.
