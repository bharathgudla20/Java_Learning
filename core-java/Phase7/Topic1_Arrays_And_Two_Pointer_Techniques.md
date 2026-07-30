# Phase 7, Topic 1: Arrays & Two-Pointer Techniques

This marks the shift into DSA and coding-round preparation — the round most product companies such as Amazon and Flipkart weigh heavily alongside Java fundamentals.

Two-pointer is one of the highest-ROI patterns to master because it can turn $O(n^2)$ brute-force solutions into $O(n)$ solutions.

## Why this matters

A large number of array and string coding questions can be solved efficiently with two pointers instead of nested loops.

Interviewers often look for whether you can recognize when two-pointer is applicable. That pattern-recognition skill is a major differentiator in a strong DSA round.

---

## 1. The Core Idea

Two-pointer is a technique where you use two index variables to traverse an array or string instead of using nested loops.

There are two common flavors:

- Opposite ends: one pointer at the start and one at the end, moving toward each other
- Same direction: both pointers start together, but one moves faster or under different conditions

---

## 2. Opposite-Ends Pattern — Example: Two Sum in Sorted Array

### Problem

Given a sorted array, find two numbers that add up to a target.

### Brute force

A nested loop checks every pair, which is $O(n^2)$.

### Two-pointer solution

```java
public int[] twoSumSorted(int[] arr, int target) {
    int left = 0, right = arr.length - 1;
    while (left < right) {
        int sum = arr[left] + arr[right];
        if (sum == target) {
            return new int[]{left, right};
        } else if (sum < target) {
            left++;      // need a bigger sum -> move left pointer up
        } else {
            right--;     // need a smaller sum -> move right pointer down
        }
    }
    return new int[]{-1, -1};
}
```

### Why this works

Because the array is sorted, if the current sum is too small, moving `left` forward is the only way to increase the sum. Moving `right` backward would only decrease it further.

This eliminates the need for nested loops.

---

## 3. Opposite-Ends Pattern — Example: Valid Palindrome

```java
public boolean isPalindrome(String s) {
    int left = 0, right = s.length() - 1;
    while (left < right) {
        if (s.charAt(left) != s.charAt(right)) {
            return false;
        }
        left++;
        right--;
    }
    return true;
}
```

This is a very common warm-up question and shows that the pattern works beyond numeric problems.

---

## 4. Same-Direction Pattern — Example: Remove Duplicates from Sorted Array

### Problem

Given a sorted array, remove duplicates in-place and return the new length.

```java
public int removeDuplicates(int[] arr) {
    if (arr.length == 0) return 0;
    int slow = 0;
    for (int fast = 1; fast < arr.length; fast++) {
        if (arr[fast] != arr[slow]) {
            slow++;
            arr[slow] = arr[fast];
        }
    }
    return slow + 1;
}
```

### Walkthrough on `[1,1,2,2,3]`

- `slow = 0, fast = 1`: `arr[1] == arr[0]` → skip
- `fast = 2`: `arr[2] != arr[0]` → `slow = 1`, `arr[1] = 2`
- `fast = 3`: `arr[3] == arr[1]` → skip
- `fast = 4`: `arr[4] != arr[1]` → `slow = 2`, `arr[2] = 3`
- Result: length is `3`, and the first three elements are `[1, 2, 3]`

### Key idea

`slow` tracks where the next unique element should go, while `fast` scans ahead looking for it. This is an in-place technique and avoids using extra space.

---

## 5. Same-Direction Pattern — Example: Move Zeroes to End

### Problem

Move all zeroes in an array to the end while keeping the relative order of non-zero elements.

```java
public void moveZeroes(int[] arr) {
    int slow = 0;
    for (int fast = 0; fast < arr.length; fast++) {
        if (arr[fast] != 0) {
            int temp = arr[slow];
            arr[slow] = arr[fast];
            arr[fast] = temp;
            slow++;
        }
    }
}
```

`slow` marks the boundary of the processed non-zero region. Every time `fast` finds a non-zero value, it is swapped into that boundary position.

---

## 6. Three-Pointer Variant — Example: Sort Colors (Dutch National Flag)

This is a common interview problem where you sort an array of `0`s, `1`s, and `2`s in one pass.

```java
public void sortColors(int[] arr) {
    int low = 0, mid = 0, high = arr.length - 1;
    while (mid <= high) {
        if (arr[mid] == 0) {
            swap(arr, low++, mid++);
        } else if (arr[mid] == 1) {
            mid++;
        } else {
            swap(arr, mid, high--);
        }
    }
}

private void swap(int[] arr, int i, int j) {
    int temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
}
```

### Why `mid` is not incremented after swapping with `high`

The element swapped in from the `high` side has not been checked yet. It could be `0`, `1`, or `2`, so `mid` must examine it again.

---

## 7. When to Recognize This Pattern

Ask yourself these signals during a problem:

- Is the array sorted (or can it be sorted without losing important information)? → opposite-ends is likely applicable.
- Do I need to find a pair or triplet that matches a condition such as sum or difference? → opposite-ends.
- Am I doing in-place modification such as removing or moving elements while preserving order? → same-direction is likely useful.
- Is my brute-force solution $O(n^2)$ with nested loops scanning the same array repeatedly? → two-pointer is likely the right optimization.
