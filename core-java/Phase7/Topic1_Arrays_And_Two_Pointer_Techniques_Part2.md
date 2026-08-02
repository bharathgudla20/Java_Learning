# Phase 7 - Topic 1: Arrays and Two Pointer Techniques (Part 2)

## 1. Sorting

### Definition
Arranging array elements in a specific order (ascending or descending) — often the first step that unlocks other techniques like two-pointer or binary search.

### Java's built-in sort

```java
int[] arr = {5, 2, 8, 1, 9};
Arrays.sort(arr);                          // ascending, O(n log n)
Arrays.sort(arr, Collections.reverseOrder()); // only works on Integer[], not int[]

Integer[] boxed = {5, 2, 8, 1, 9};
Arrays.sort(boxed, Collections.reverseOrder()); // descending
```

### Interview-relevant fact
- `Arrays.sort()` on primitives uses Dual-Pivot Quicksort (average O(n log n), worst-case O(n²)).
- On objects like `Integer[]`, it uses a variant of TimSort (stable, O(n log n) worst case).

### Custom sorting with Comparator

```java
List<Employee> employees = ...;
employees.sort((e1, e2) -> e1.getSalary() - e2.getSalary());
employees.sort(Comparator.comparing(Employee::getSalary).reversed());
```

### Why it matters
Two-pointer and binary search often require sorted input, so interviewers expect you to say “let’s sort first” naturally.

---

## 2. Binary Search

### Definition
A divide-and-conquer search algorithm on a sorted array that repeatedly halves the search space and gives O(log n) time.

```java
public int binarySearch(int[] arr, int target) {
    int left = 0, right = arr.length - 1;
    while (left <= right) {
        int mid = left + (right - left) / 2;
        if (arr[mid] == target) {
            return mid;
        } else if (arr[mid] < target) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }
    return -1;
}
```

### Interview traps
- `mid = (left + right) / 2` can overflow for large arrays.
- Using `while (left < right)` with incorrect boundary updates can create an infinite loop.

### Find first/last occurrence

```java
public int findFirstOccurrence(int[] arr, int target) {
    int left = 0, right = arr.length - 1, result = -1;
    while (left <= right) {
        int mid = left + (right - left) / 2;
        if (arr[mid] == target) {
            result = mid;
            right = mid - 1;
        } else if (arr[mid] < target) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }
    return result;
}
```

### Key idea
Even after finding a match, keep narrowing in the direction you need: left for the first occurrence, right for the last.

### Built-in Java version
```java
Arrays.binarySearch(arr, target);
```
It returns the index if found, or `-(insertion point) - 1` if not found.

---

## 3. Sliding Window

### Definition
A technique for problems involving contiguous subarrays or substrings where a window is maintained and moved across the array to avoid recomputing from scratch.

### Two common flavors
- Fixed-size window
- Variable-size window

### Fixed-size example: max sum of subarray of size k

```java
public int maxSumSubarray(int[] arr, int k) {
    int windowSum = 0;
    for (int i = 0; i < k; i++) windowSum += arr[i];

    int maxSum = windowSum;
    for (int end = k; end < arr.length; end++) {
        windowSum += arr[end] - arr[end - k];
        maxSum = Math.max(maxSum, windowSum);
    }
    return maxSum;
}
```

### Variable-size example: longest substring without repeating characters

```java
public int lengthOfLongestSubstring(String s) {
    Set<Character> window = new HashSet<>();
    int left = 0, maxLen = 0;
    for (int right = 0; right < s.length(); right++) {
        while (window.contains(s.charAt(right))) {
            window.remove(s.charAt(left));
            left++;
        }
        window.add(s.charAt(right));
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
```

### Key idea
`right` expands the window, while `left` moves forward only when the window becomes invalid.

---

## 4. Prefix Sum

### Definition
Precompute cumulative sums so that the sum of any subarray range can be answered in O(1) instead of recomputing each time.

```java
public int[] buildPrefixSum(int[] arr) {
    int[] prefix = new int[arr.length + 1];
    for (int i = 0; i < arr.length; i++) {
        prefix[i + 1] = prefix[i] + arr[i];
    }
    return prefix;
}

public int rangeSum(int[] prefix, int left, int right) {
    return prefix[right + 1] - prefix[left];
}
```

### Why `+1` is used
`prefix[i]` means “sum of the first `i` elements,” so `prefix[0] = 0` avoids edge-case checks for `left == 0`.

### Example
For `arr = [2, 4, 6, 8, 10]`, the prefix sum array is:

```java
[0, 2, 6, 12, 20, 30]
```

The sum of range `[1, 3]` is:

```java
prefix[4] - prefix[1] = 20 - 2 = 18
```

### Common use case: Subarray Sum Equals K

```java
public int subarraySum(int[] arr, int k) {
    Map<Integer, Integer> prefixCount = new HashMap<>();
    prefixCount.put(0, 1);
    int sum = 0, count = 0;
    for (int num : arr) {
        sum += num;
        count += prefixCount.getOrDefault(sum - k, 0);
        prefixCount.merge(sum, 1, Integer::sum);
    }
    return count;
}
```

### Key insight
If `sum - k` has appeared before as a prefix sum, then the segment between that earlier point and the current point sums to exactly `k`.

---

## Quick Comparison Table

| Subtopic | Solves | Time Complexity | Requires Sorted Input? |
|---|---|---:|---|
| Sorting | Ordering elements and enabling other techniques | O(n log n) | No |
| Binary Search | Finding a target or boundary in sorted data | O(log n) | Yes |
| Sliding Window | Contiguous subarray/substring problems | O(n) | No |
| Prefix Sum | Fast repeated range-sum queries | O(n) build, O(1) per query | No |
