# Maximum Twin Sum of a Linked List

## Problem Statement

In a linked list of size `n`, where `n` is even, the `ith` node (0-indexed) of the linked list is known as the twin of the `(n-1-i)th` node.
The twin sum is defined as the sum of a node and its twin.

Given the `head` of a linked list with even length, return the maximum twin sum of the linked list.

## Example

**Input:**
```
head = [5,4,2,1]
```
**Output:**
```
6
```

**Explanation:**
Nodes 0 and 1 are the twins of nodes 3 and 2, respectively. Each twin sum is 6, so the maximum twin sum is 6.

## Solution

```java
class Solution {
    public int pairSum(ListNode head) {
        ListNode slow = head, fast = head, prev = null;

        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            ListNode temp = slow.next;
            slow.next = prev;
            prev = slow;
            slow = temp;
        }

        int res = 0;
        while (slow != null) {
            res = Math.max(res, prev.val + slow.val);
            prev = prev.next;
            slow = slow.next;
        }

        return res;
    }
}
```

## Approach

1. Use Floyd's Tortoise and Hare algorithm to find the midpoint of the list.
2. While advancing the `slow` pointer one step at a time, reverse the first half of the list.
3. When `fast` reaches the end, `slow` will be at the start of the second half, and `prev` will point to the reversed first half.
4. Traverse both halves simultaneously and compute twin sums by adding corresponding node values.
5. Track the maximum twin sum and return it.

## Complexity Analysis

- Time Complexity: O(n)
- Space Complexity: O(1)

## Notes

- The linked list length is guaranteed to be even.
- Reversing the first half in place allows constant extra space.
- The second traversal compares reversed first-half nodes with second-half nodes in twin order.
