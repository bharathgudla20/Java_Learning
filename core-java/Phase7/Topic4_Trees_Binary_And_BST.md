# Trees — Binary & BST

## Why this matters

Trees test recursive thinking more than any other data structure. Once you're comfortable with the basic traversal template, most tree problems become variations on the same recursive pattern — interviewers are really testing whether you can adapt that pattern to new conditions.

---

## 0. Quick Setup — Node Definition

```java
class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int val) {
        this.val = val;
    }
}
```

### Binary Tree vs BST

- Binary Tree: each node has at most 2 children, no ordering rule.
- BST: a binary tree where for every node:
  - everything in the left subtree is smaller
  - everything in the right subtree is larger

This distinction matters a lot because the same tree problem may have different solutions depending on whether the tree is a normal binary tree or a BST.

---

## 1. Traversals

Definition: systematic ways to visit every node in a tree exactly once. There are three DFS orders and one BFS order.

### DFS Traversals

These differ only in when the current node is visited relative to its children.

#### Inorder: Left → Node → Right

This gives sorted order for a BST, which is a very important fact.

```java
public void inorder(TreeNode root, List<Integer> result) {
    if (root == null) return;
    inorder(root.left, result);
    result.add(root.val);
    inorder(root.right, result);
}
```

#### Preorder: Node → Left → Right

Useful for copying or serializing a tree.

```java
public void preorder(TreeNode root, List<Integer> result) {
    if (root == null) return;
    result.add(root.val);
    preorder(root.left, result);
    preorder(root.right, result);
}
```

#### Postorder: Left → Right → Node

Useful for deleting a tree or computing values bottom-up.

```java
public void postorder(TreeNode root, List<Integer> result) {
    if (root == null) return;
    postorder(root.left, result);
    postorder(root.right, result);
    result.add(root.val);
}
```

### Key fact interviewers love asking

The inorder traversal of a BST always produces values in sorted ascending order.

This is the defining property used in problems like Validate BST.

### BFS Traversal — Level Order

Uses a queue instead of recursion.

```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        List<Integer> level = new ArrayList<>();

        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);

            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }

        result.add(level);
    }
    return result;
}
```

### Why `levelSize` is important

The queue grows as you add children, so if you use `queue.size()` directly in the loop, you are not processing exactly one level at a time. Saving `levelSize` first ensures each iteration handles one level only.

---

## 2. Height (a.k.a. Max Depth)

Definition: the number of edges (or nodes, depending on convention) on the longest path from the root to a leaf.

```java
public int height(TreeNode root) {
    if (root == null) return 0;

    int leftHeight = height(root.left);
    int rightHeight = height(root.right);
    return 1 + Math.max(leftHeight, rightHeight);
}
```

### Recursive pattern to internalize

The answer for a node is usually built from the answers of its children.

This is the classic bottom-up pattern:

- solve left subtree
- solve right subtree
- combine them at the current node

Height problems and many tree problems follow this structure.

### Common follow-up — Check if a tree is height-balanced

```java
public boolean isBalanced(TreeNode root) {
    return checkHeight(root) != -1;
}

private int checkHeight(TreeNode node) {
    if (node == null) return 0;

    int left = checkHeight(node.left);
    if (left == -1) return -1;

    int right = checkHeight(node.right);
    if (right == -1) return -1;

    if (Math.abs(left - right) > 1) return -1;

    return 1 + Math.max(left, right);
}
```

### Why return `-1` as a sentinel?

Instead of computing height, then checking balance separately, this combines both into a single pass.

- normal height means subtree is balanced
- `-1` means “this subtree is already unbalanced”

So once you detect imbalance, you stop exploring unnecessary work.

This gives `O(n)` instead of the slower repeated-height approach.

---

## 3. LCA — Lowest Common Ancestor

Definition: given two nodes in a tree, find the deepest node that is an ancestor of both nodes.

### General Binary Tree version

This does not assume BST ordering.

```java
public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) return root;

    TreeNode left = lowestCommonAncestor(root.left, p, q);
    TreeNode right = lowestCommonAncestor(root.right, p, q);

    if (left != null && right != null) return root;
    return (left != null) ? left : right;
}
```

### Why this works

If `p` is found in the left subtree and `q` is found in the right subtree, then the current node is the exact split point. That node is the LCA.

If both are found on the same side, we bubble that answer upward.

### BST version

Because BST has ordering, we can use a more efficient approach.

```java
public TreeNode lowestCommonAncestorBST(TreeNode root, TreeNode p, TreeNode q) {
    if (p.val < root.val && q.val < root.val) {
        return lowestCommonAncestorBST(root.left, p, q);
    } else if (p.val > root.val && q.val > root.val) {
        return lowestCommonAncestorBST(root.right, p, q);
    } else {
        return root;
    }
}
```

### Why this is faster

For a BST, you can decide which subtree to search using value comparisons. That avoids exploring both sides and reduces the complexity to `O(h)`, where `h` is the tree height.

---

## 4. Validate BST

Definition: check whether a given binary tree satisfies the BST property at every node, not just immediate children.

### Common mistake (classic interview trap)

```java
public boolean isValidBST_WRONG(TreeNode root) {
    if (root == null) return true;
    if (root.left != null && root.left.val >= root.val) return false;
    if (root.right != null && root.right.val <= root.val) return false;
    return isValidBST_WRONG(root.left) && isValidBST_WRONG(root.right);
}
```

This fails because it only checks the immediate parent-child relationship, not the whole subtree.

Example:

```text
        10
       /  \
      5    15
          /  \
         6    20
```

Here, `6` is in the right subtree of `10`, but it is less than `10` — this is invalid, yet the naive check doesn’t detect it because it only looked at `6` vs `15`.

### Correct approach 1 — Pass down a valid range

```java
public boolean isValidBST(TreeNode root) {
    return validate(root, null, null);
}

private boolean validate(TreeNode node, Integer min, Integer max) {
    if (node == null) return true;

    if ((min != null && node.val <= min) || (max != null && node.val >= max)) {
        return false;
    }

    return validate(node.left, min, node.val)
        && validate(node.right, node.val, max);
}
```

### Key idea

- When moving left, the upper bound becomes the current node value.
- When moving right, the lower bound becomes the current node value.

This ensures every node respects the global BST range, not just local parent-child constraints.

### Correct approach 2 — Use inorder is sorted

This ties directly back to traversal logic.

```java
private Integer prev = null;

public boolean isValidBSTInorder(TreeNode root) {
    if (root == null) return true;

    if (!isValidBSTInorder(root.left)) return false;

    if (prev != null && root.val <= prev) return false;
    prev = root.val;

    return isValidBSTInorder(root.right);
}
```

### Why this works

For a valid BST, inorder traversal gives strictly increasing values. If you ever see a value that is not greater than the previous one, then the tree is invalid.

---

## 5. Mental Model for Tree Problems

Most tree questions follow this pattern:

1. Handle base case (`null`)
2. Solve left subtree
3. Solve right subtree
4. Combine answers at the current node

This is the heart of recursion in trees.

A useful interview mindset is:

> “What does the current node need to know from its children before it can decide its own answer?”

That usually leads directly to the correct recursive solution.

---

## Final Takeaways

- BST has ordering; Binary Tree does not.
- Inorder traversal of BST is sorted.
- Level-order traversal uses queue.
- Height is solved bottom-up.
- LCA can be solved recursively in both tree and BST cases.
- Validation requires range tracking or inorder checking.

These ideas are the foundation of most tree interview problems.
