# Sequential Digits

## Problem

An integer has sequential digits if and only if each digit in the number is one more than the previous digit.

Return a sorted list of all integers in the range `[low, high]` inclusive that have sequential digits.

### Example 1

```text
Input: low = 100, high = 300
Output: [123, 234]
```

### Example 2

```text
Input: low = 1000, high = 13000
Output: [1234, 2345, 3456, 4567, 5678, 6789, 12345]
```

---

## Java Solution

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Solution {
    public List<Integer> sequentialDigits(int low, int high) {
        List<Integer> ans = new ArrayList<>();

        for (int start = 1; start <= 9; start++) {
            int num = 0;
            for (int d = start; d <= 9; d++) {
                num = num * 10 + d;
                if (num >= low && num <= high) {
                    ans.add(num);
                }
            }
        }

        Collections.sort(ans);
        return ans;
    }
}
```

---

## Explanation

We generate all possible sequential numbers by starting from each digit `1` to `9` and building numbers like:

- `1, 12, 123, 1234, ...`
- `2, 23, 234, 2345, ...`
- `3, 34, 345, 3456, ...`

If a generated number lies within the given range, we add it to the answer.

Finally, we sort the list and return it.

---

## Complexity

- Time: `O(1)` for this problem size, since only a small fixed set of sequential numbers exists.
- Space: `O(1)` extra space (excluding the output list).
