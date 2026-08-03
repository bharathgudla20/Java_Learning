# Linked List Patterns

## Why this matters

Linked list problems test whether you can manipulate references correctly without losing nodes or creating cycles by accident. These four patterns (reverse, cycle detection, merge, find middle) form the building blocks for almost every other linked-list question you'll be asked.

---

## 0. Quick Setup — Node Definition

```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}
```

---

## 1. Reverse a Linked List

**Definition:** Reverse the direction of all `next` pointers so the list traverses in the opposite order, in-place (O(1) extra space).

```java
public ListNode reverseList(ListNode head) {
    ListNode prev = null;
    ListNode curr = head;
    while (curr != null) {
        ListNode nextTemp = curr.next;  // save next before overwriting it
        curr.next = prev;               // reverse the pointer
        prev = curr;                    // move prev forward
        curr = nextTemp;                // move curr forward
    }
    return prev;   // prev is the new head
}
```

**Walkthrough on `1 → 2 → 3 → null`:**

- `prev=null, curr=1`: save `next=2`, `1.next=null`, `prev=1`, `curr=2`
- `prev=1, curr=2`: save `next=3`, `2.next=1`, `prev=2`, `curr=3`
- `prev=2, curr=3`: save `next=null`, `3.next=2`, `prev=3`, `curr=null`
- Loop ends, return `prev=3` → list is now `3 → 2 → 1 → null` ✅

**Why saving `nextTemp` first is critical:** if you write `curr.next = prev` *before* saving the original `curr.next`, you permanently lose the rest of the list — there's no way to reach it anymore.

**Recursive version:**

```java
public ListNode reverseListRecursive(ListNode head) {
    if (head == null || head.next == null) return head;   // base case
    ListNode newHead = reverseListRecursive(head.next);
    head.next.next = head;   // make the next node point back to current
    head.next = null;        // break the old forward link
    return newHead;
}
```

---

## 2. Detect Cycle — Floyd's Cycle Detection (Tortoise and Hare)

**Definition:** Determine if a linked list has a cycle (a node's `next` eventually loops back to a previous node) using **two pointers moving at different speeds** — O(1) space, no extra data structure needed.

```java
public boolean hasCycle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;          // moves 1 step
        fast = fast.next.next;     // moves 2 steps
        if (slow == fast) {
            return true;   // they met → cycle exists
        }
    }
    return false;   // fast reached the end → no cycle
}
```

**Why this works:** if there's a cycle, the fast pointer (moving 2x speed) will eventually "lap" the slow pointer inside the loop and they'll land on the same node. If there's no cycle, `fast` simply reaches `null` first.

**Common follow-up — Find the start of the cycle:**

```java
public ListNode detectCycleStart(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) {
            // found intersection — now find cycle start
            ListNode ptr = head;
            while (ptr != slow) {
                ptr = ptr.next;
                slow = slow.next;
            }
            return ptr;   // this is the start of the cycle
        }
    }
    return null;
}
```

**Why this works:** the distance from `head` to the cycle start equals the distance from the meeting point to the cycle start (going around the loop).

---

## 3. Merge Two Sorted Linked Lists

**Definition:** Combine two already-sorted linked lists into a single sorted linked list, reusing existing nodes (no extra list allocation for values).

```java
public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(-1);   // dummy node simplifies edge cases
    ListNode tail = dummy;

    while (l1 != null && l2 != null) {
        if (l1.val <= l2.val) {
            tail.next = l1;
            l1 = l1.next;
        } else {
            tail.next = l2;
            l2 = l2.next;
        }
        tail = tail.next;
    }
    tail.next = (l1 != null) ? l1 : l2;   // attach whichever list has leftovers
    return dummy.next;   // skip the dummy node itself
}
```

**Why the dummy node trick matters:** without it, you'd need special-case code to initialize `head` on the very first comparison. The dummy node gives you a consistent `tail` to attach to from the start.

---

## 4. Find the Middle of a Linked List

**Definition:** Find the middle node in a single pass using the **slow/fast pointer** technique.

```java
public ListNode findMiddle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;        // 1 step
        fast = fast.next.next;   // 2 steps
    }
    return slow;   // when fast reaches the end, slow is at the middle
}
```

**Why this works:** by the time `fast` (moving 2x speed) reaches the end, `slow` (moving 1x speed) has covered exactly half the distance — landing on the middle.

**Even-length lists:** for `1→2→3→4`, this returns node `3` (the **second** middle). If you want the **first** middle instead, use `while (fast.next != null && fast.next.next != null)`.

**Real use case:** this pattern is the foundation for merge sort on linked lists and palindrome linked list checks.

---

## Quick Comparison Table

Operation | Technique | Time | Space
--- | --- | --- | ---
Reverse | 3-pointer walk (prev/curr/next) | O(n) | O(1)
Detect Cycle | Slow/fast pointers (Floyd's) | O(n) | O(1)
Merge Two Sorted Lists | Two pointers + dummy node | O(n+m) | O(1)
Find Middle | Slow/fast pointers | O(n) | O(1)
