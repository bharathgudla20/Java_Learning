# Delete Middle Node of a Linked List

## Problem Statement
Given the head of a linked list, delete the middle node and return the head of the modified linked list.

The middle node of a linked list of size `n` is the `⌊n / 2⌋`th node from the start using 0-based indexing.

### Examples:
- **Example 1:** `[1,3,4,7,1,2,6]` → `[1,3,4,1,2,6]` (n=7, middle at index 3 with value 7)
- **Example 2:** `[1,2,3,4]` → `[1,2,4]` (n=4, middle at index 2 with value 3)

---

## Solution Approach

### Two-Pointer Technique (Optimal)
Use a **fast and slow pointer** approach:
- **Slow pointer** moves 1 step at a time
- **Fast pointer** moves 2 steps at a time
- When the fast pointer reaches the end, the slow pointer will be just before the middle node
- We use a **dummy node** to handle the edge case where we need to delete the head

### Algorithm:
1. Create a dummy node pointing to head
2. Initialize slow pointer at dummy, fast pointer at head
3. Move slow 1 step and fast 2 steps until fast reaches end
4. Delete the middle node: `slow.next = slow.next.next`
5. Return `dummy.next`

---

## Java Implementation

```java
/**
 * Definition for singly-linked list node.
 */
class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

class Solution {
    public ListNode deleteMiddle(ListNode head) {
        // Edge case: single node
        if (head == null || head.next == null) {
            return null;
        }
        
        // Create a dummy node pointing to head to handle deletion of head
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        
        ListNode slow = dummy;  // Will be before the middle node
        ListNode fast = head;   // Moves twice as fast
        
        // Move slow one step and fast two steps
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        
        // Delete the middle node
        slow.next = slow.next.next;
        
        return dummy.next;
    }
}
```

---

## Alternative Approach (Without Dummy Node)

```java
class Solution {
    public ListNode deleteMiddle(ListNode head) {
        // Edge case: single node
        if (head.next == null) {
            return null;
        }
        
        ListNode fast = head;
        ListNode slow = head;
        ListNode prev = slow;
        
        // Move fast two steps and slow one step until fast reaches end
        while (fast.next != null) {
            if (fast.next.next != null) {
                fast = fast.next.next;
            } else {
                fast = fast.next;
            }
            prev = slow;
            slow = slow.next;
        }
        
        // Delete the middle node
        prev.next = slow.next;
        
        return head;
    }
}
```

### How It Works:
- Uses `prev` pointer to track the node before the middle node
- Uses conditional check `if(fast.next.next != null)` to safely advance fast pointer
- When loop ends, `slow` points to the middle node and `prev` points to its predecessor
- Deletes by: `prev.next = slow.next`
- Returns original head without needing a dummy node

---

## Approach Comparison

| Feature | Approach 1 (Dummy Node) | Approach 2 (No Dummy) |
|---------|------------------------|----------------------|
| **Complexity** | O(n) time, O(1) space | O(n) time, O(1) space |
| **Uses Dummy Node** | ✅ Yes | ❌ No |
| **Track Previous Node** | ❌ No (implicit via dummy) | ✅ Yes (explicit prev) |
| **Handle Head Deletion** | ✅ Automatic | ⚠️ Returns original head |
| **Code Simplicity** | ✅ Cleaner logic | More conditional checks |
| **Fast Pointer Handling** | Direct `fast.next.next` | Conditional safety check |

### When to Use:
- **Approach 1**: Preferred for interviews - cleaner, fewer edge cases to handle
- **Approach 2**: Direct approach without dummy node overhead; requires tracking prev explicitly

---

## Complexity Analysis

| Aspect | Complexity |
|--------|-----------|
| **Time** | O(n) - single pass through the list |
| **Space** | O(1) - only using constant extra space |

---

## Walkthrough with Example

### Example: `[1,3,4,7,1,2,6]` (n=7, middle at index 3)

```
Initial: dummy -> 1 -> 3 -> 4 -> 7 -> 1 -> 2 -> 6 -> null

Step 1:
slow: dummy     fast: 1
Step 2:
slow: 1         fast: 4
Step 3:
slow: 3         fast: 1
Step 4:
slow: 4         fast: 6
Step 5 (fast.next == null, exit loop):
slow: 7 (before middle node)

Action: slow.next = slow.next.next
        7.next = 7.next.next (which is 1)

Result: dummy -> 1 -> 3 -> 4 -> 1 -> 2 -> 6 -> null
Return: 1 -> 3 -> 4 -> 1 -> 2 -> 6 -> null
```

---

## Key Points

1. **Dummy Node**: Using a dummy node makes it easier to handle the case where we might need to delete the head
2. **Two-Pointer Technique**: Efficiently finds the node before the middle in a single pass
3. **Edge Cases**:
   - Single node list: delete it, return null
   - Two nodes: delete the second node
   - For n=1: ⌊1/2⌋ = 0 (delete head)

---

## Related Problems
- 🔗 [Maximum Twin Sum of a Linked List](./Maximum_Twin_Sum_of_a_Linked_List.md)
- 🔗 Reverse Linked List
- 🔗 Reorder List
