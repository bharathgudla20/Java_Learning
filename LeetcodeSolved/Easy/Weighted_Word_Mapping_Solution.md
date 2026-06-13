# Weighted Word Mapping

## Problem Statement

You are given an array of strings `words`, where each string represents a word containing lowercase English letters.

You are also given an integer array `weights` of length 26, where `weights[i]` represents the weight of the `ith` lowercase English letter.

The **weight** of a word is defined as the **sum** of the weights of its characters.

For each word, take its weight modulo 26 and map the result to a lowercase English letter using reverse alphabetical order (`0 -> 'z', 1 -> 'y', ..., 25 -> 'a'`).

Return a string formed by concatenating the mapped characters for all words in order.

## Example

**Input:**
```
words = ["abcd","def","xyz"]
weights = [5,3,12,14,1,2,3,2,10,6,6,9,7,8,7,10,8,9,6,9,9,8,3,7,7,2]
```

**Output:**
```
"rij"
```

**Explanation:**
- The weight of `"abcd"` is `5 + 3 + 12 + 14 = 34`. The result modulo 26 is `34 % 26 = 8`, which maps to `'r'`.
- The weight of `"def"` is `14 + 1 + 2 = 17`. The result modulo 26 is `17 % 26 = 17`, which maps to `'i'`.
- The weight of `"xyz"` is `7 + 7 + 2 = 16`. The result modulo 26 is `16 % 26 = 16`, which maps to `'j'`.

Thus, the string formed by concatenating the mapped characters is `"rij"`.

## Solution

```java
class Solution {
    public String mapWordWeights(String[] words, int[] weights) {
        String ans = "";
        for(int i = 0; i < words.length; i++) {
            String word = words[i];
            int j = 0;
            int weight = 0;
            
            // Calculate the total weight of the word
            while(j < word.length()) {
                int ind = word.charAt(j) - 97;  // Convert char to index (0-25)
                weight += weights[ind];
                j++;
            }
            
            // Map weight modulo 26 to reverse alphabetical order
            int result = 97 + 25 - (weight % 26);
            char ch = (char)result;
            ans = ans + ch;
        }
        return ans;
    }
}
```

## Approach

1. **Iterate through each word** in the input array.
2. **Calculate word weight**: For each character in the word:
   - Convert the character to an index (0-25) by subtracting 97 (ASCII value of 'a')
   - Add the corresponding weight from the `weights` array
3. **Apply modulo operation**: Take the total weight modulo 26
4. **Map to reverse alphabetical order**:
   - Use formula: `ASCII = 97 + 25 - (weight % 26)`
   - This maps: 0 → 'z' (122), 1 → 'y' (121), ..., 25 → 'a' (97)
5. **Concatenate results** from all words

## Complexity Analysis

- **Time Complexity**: O(n × m), where n is the number of words and m is the average length of each word
- **Space Complexity**: O(1) excluding the output string

## Key Points

- Characters are 0-indexed using `char - 97` where 97 is the ASCII value of 'a'
- The reverse mapping formula `97 + 25 - (weight % 26)` correctly maps:
  - Remainder 0 → 'z'
  - Remainder 1 → 'y'
  - Remainder 25 → 'a'
- String concatenation could be optimized using `StringBuilder` for production code
