# Lexicographically Smallest Palindromic Permutation Greater Than Target

LeetCode Medium

## Problem

You are given two strings `s` and `target`, each of length `n`, consisting of lowercase English letters.

Return the lexicographically smallest string that is both a palindromic permutation of `s` and strictly greater than `target`. If no such permutation exists, return an empty string.

### Example 1

**Input:** `s = "baba", target = "abba"`

**Output:** `"baab"`

**Explanation:**

- The palindromic permutations of `s` (in lexicographical order) are `"abba"` and `"baab"`.
- The lexicographically smallest permutation that is strictly greater than `target` is `"baab"`.

### Example 2

**Input:** `s = "baba", target = "bbaa"`

**Output:** `""`

**Explanation:**

- The palindromic permutations of `s` (in lexicographical order) are `"abba"` and `"baab"`.
- None of them is lexicographically strictly greater than `target`. Therefore, the answer is `""`.

### Example 3

**Input:** `s = "abc", target = "abb"`

**Output:** `""`

**Explanation:**

`s` has no palindromic permutations. Therefore, the answer is `""`.

### Example 4

**Input:** `s = "aac", target = "abb"`

**Output:** `"aca"`

**Explanation:**

- The only palindromic permutation of `s` is `"aca"`.
- `"aca"` is strictly greater than `target`. Therefore, the answer is `"aca"`.

## Approach

We need the lexicographically smallest palindromic permutation of `s` that is strictly greater than `target`.

First, we need to check whether `s` can form a palindrome at all.

- A palindrome can be formed only when at most one character has an odd frequency.
- If more than one character has odd frequency, the answer is immediately `""`.

A palindrome has this structure:

```
left half + middle + reverse(left half)
```

So instead of constructing the whole palindrome directly, we only need to decide the left half. If `n` is odd, one character stays in the middle. For the left half, each character can be used `freq[i] / 2` times.

Now the main challenge is making the palindrome:

- Greater than `target`
- As small as possible

We build the left half from left to right. At every position, we try characters from `'a'` to `'z'`. We temporarily place one character.

Then we ask:

> With this prefix fixed, is it still possible to make a palindrome greater than target?

To answer this, `isPossible()` constructs the largest possible palindrome from the remaining characters by putting the remaining left-half characters in descending order.

Why the largest one? If even the largest possible palindrome with our current prefix is not greater than `target`, then no smaller arrangement can work either. So this gives us a very useful feasibility check. Once a character is found to be feasible, we keep it and move to the next position.

## Java Solution

```java
class Solution {
    public String isPossible(int n, int[] freqIn, String cur, char mid, String target){
        int[] freq = freqIn.clone(); // copy

        // build the largest possible arrangement of remaining chars (descending order)
        for(int i=25; i>=0; i--){
            while(freq[i] > 0){
                cur += (char)('a'+i);
                freq[i]--;
            }
        }

        if(mid!='#'){
            // odd-length palindrome: left half + mid + reverse(left half)
            String temp = cur;
            cur += mid;
            temp = new StringBuilder(temp).reverse().toString();
            cur += temp;
        }
        else {
            // even-length palindrome: left half + reverse(left half)
            String temp = cur;
            temp = new StringBuilder(temp).reverse().toString();
            cur += temp;
        }

        // feasibility check: only valid if this (largest possible) candidate beats target
        return cur.compareTo(target) > 0 ? cur : "";
    }

    public String lexPalindromicPermutation(String s, String target) {
        int n = s.length();

        int[] freq = new int[26];

        if(n==1){
            if(s.compareTo(target) > 0) return s;
            else return "";
        }

        for(char c : s.toCharArray())
            freq[c-'a']++;

        char mid = '#';
        int oddCount = 0;

        for(int i=0; i<26; i++){
            if(freq[i]%2 != 0){
                // odd count -> this becomes the middle character
                mid = (char)('a'+i);
                freq[i]--;
                oddCount++;
            }

            freq[i] /= 2; // each char used freq[i]/2 times in the left half

            if(oddCount>=2) return ""; // more than one odd-frequency char -> can't form a palindrome
        }

        n /= 2; // we only need to construct the left half now

        String res = "", prefix = "";

        // greedily build the left half, position by position
        for(int i=0; i<n; i++){

            String cur = prefix;
            boolean isThereAny = false;

            // try smallest character first ('a' -> 'z')
            for(int j=0; j<26; j++){

                if(freq[j] > 0){

                    freq[j]--;
                    cur += (char)('a'+j);

                    // check if this prefix can still lead to a palindrome > target
                    String isPos = isPossible(n, freq, cur, mid, target);

                    if(!isPos.equals("")){
                        prefix = cur;      // keep this character, lock in the prefix
                        isThereAny = true;

                        if(res.equals(""))
                            res = isPos;
                        else
                            res = res.compareTo(isPos) < 0 ? res : isPos; // track smallest valid candidate

                        break;
                    }

                    // this character doesn't work, undo and try the next one
                    freq[j]++;
                    cur = cur.substring(0, cur.length()-1);
                }
            }

            if(!isThereAny)
                return ""; // no character works at this position -> impossible
        }

        return res; 
    }
}
```
