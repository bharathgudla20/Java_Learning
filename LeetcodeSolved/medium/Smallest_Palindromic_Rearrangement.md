# Smallest Palindromic Rearrangement

## Problem
You are given a palindromic string `s`.

Return the lexicographically smallest palindromic permutation of `s`.

### Example 1
- Input: `s = "z"`
- Output: `"z"`

### Example 2
- Input: `s = "babab"`
- Output: `"abbba"`

### Example 3
- Input: `s = "daccad"`
- Output: `"acddca"`

## Approach
1. Count the frequency of each character.
2. Build the left half by adding `frequency / 2` copies of each character in sorted order.
3. If a character has an odd frequency, store it as the middle character.
4. Reverse the left half to form the right half.
5. Return `left + middle + reverse(left)`.

Because the left half is built in sorted order, the final palindrome will be the lexicographically smallest possible.

## Java Solution
```java
class Solution {
    public String smallestPalindrome(String s) {
        int[] freq = new int[26];

        for (char ch : s.toCharArray()) {
            freq[ch - 'a']++;
        }

        StringBuilder left = new StringBuilder();
        String middle = "";

        for (int i = 0; i < 26; i++) {
            char ch = (char) ('a' + i);
            int count = freq[i];

            for (int j = 0; j < count / 2; j++) {
                left.append(ch);
            }

            if (count % 2 == 1) {
                middle = String.valueOf(ch);
            }
        }

        String right = left.reverse().toString();
        return left.reverse().toString() + middle + right;
    }
}
```

## Complexity
- Time Complexity: `O(n)`
- Space Complexity: `O(n)`
