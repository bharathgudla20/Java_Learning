# Count Commas in Range

## Problem Statement

You are given an integer `n`.

Return the **total** number of commas used when writing all integers from `[1, n]` (inclusive) in **standard** number formatting.

In **standard** formatting:

- A comma is inserted after **every three** digits from the right.
- Numbers with **fewer** than 4 digits contain no commas.

## Examples

**Example 1:**

```text
Input: n = 1002
Output: 3
```

**Explanation:**

The numbers `"1,000"`, `"1,001"`, and `"1,002"` each contain one comma, giving a total of 3.

**Example 2:**

```text
Input: n = 998
Output: 0
```

**Explanation:**

All numbers from 1 to 998 have fewer than four digits. Therefore, no commas are used.

## Java Solution

```java
class Solution {
    public int countCommas(int n) {
        if(n<1000)
        {
            return 0;
        }
        if(n<10000)
        {
            return n-999;
        }
        if(n<100000)
        {
            return 9999-999+(n-9999);
        }
        return 99001;
    }
}
```
