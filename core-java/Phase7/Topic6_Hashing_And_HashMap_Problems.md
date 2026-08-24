# Phase 7, Topic 6: Hashing & HashMap Problems

## Why this matters

HashMap gives O(1) average lookup/insert — trading space for time. A massive fraction of "optimize this" interview questions boil down to "use a HashMap to avoid a nested loop." Recognizing this pattern quickly is a big signal of interview readiness.

---

## 1. Two Sum — the canonical hashing problem

**Definition:** Given an array and a target, find two indices whose values sum to the target — using a single pass with a HashMap instead of nested loops.

**Brute force:** O(n²) — check every pair.

**HashMap solution — O(n):**

```java
public int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> seen = new HashMap<>();  // value → index
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        if (seen.containsKey(complement)) {
            return new int[]{seen.get(complement), i};
        }
        seen.put(nums[i], i);
    }
    return new int[]{-1, -1};
}
```

**Walkthrough on `[2, 7, 11, 15]`, target `9`:**

- `i=0` (`2`): complement = `7`, not in map → put `{2:0}`
- `i=1` (`7`): complement = `2`, found in map (`2→0`) → return `[0, 1]` ✅

**Why this beats sorting + two-pointer** (an important distinction to raise in interviews): the two-pointer version from Phase 7 Topic 1 needs the array sorted first (O(n log n)) and returns values, losing original indices unless you track them separately. The HashMap version is O(n) overall and preserves indices naturally — better when the problem asks for original positions.

**Key insight:** Instead of asking "is there some j such that `nums[i] + nums[j] == target`," flip it to "have I already seen the number `target - nums[i]`?" — this reframing from "search for a pair" to "look up a complement" is the general hashing mindset.

---

## 2. Anagram Check

**Definition:** Determine if two strings are anagrams — contain exactly the same characters with the same frequencies, just rearranged.

### Approach 1 — Sorting (simple, O(n))

```java
public boolean isAnagramSort(String s, String t) {
    if (s.length() != t.length()) return false;
    char[] sArr = s.toCharArray();
    char[] tArr = t.toCharArray();
    Arrays.sort(sArr);
    Arrays.sort(tArr);
    return Arrays.equals(sArr, tArr);
}
```

> Note: Sorting is O(n log n), not O(n).

### Approach 2 — Frequency Map (better, O(n))

```java
public boolean isAnagram(String s, String t) {
    if (s.length() != t.length()) return false;

    Map<Character, Integer> freq = new HashMap<>();
    for (char c : s.toCharArray()) {
        freq.merge(c, 1, Integer::sum);   // increment count
    }
    for (char c : t.toCharArray()) {
        if (!freq.containsKey(c) || freq.get(c) == 0) return false;
        freq.merge(c, -1, Integer::sum);  // decrement count
    }
    return true;   // all counts balanced back to zero (implicitly)
}
```

### Even simpler — array-based frequency counter

This is best when the charset is known, for example, lowercase English letters:

```java
public boolean isAnagramArray(String s, String t) {
    if (s.length() != t.length()) return false;
    int[] count = new int[26];
    for (int i = 0; i < s.length(); i++) {
        count[s.charAt(i) - 'a']++;   // increment for s
        count[t.charAt(i) - 'a']--;   // decrement for t
    }
    for (int c : count) {
        if (c != 0) return false;     // any leftover means mismatch
    }
    return true;
}
```

**Why the array version is often preferred** (worth mentioning as an optimization): for a known, small alphabet (like lowercase English letters), a fixed-size `int[26]` avoids HashMap overhead (hashing, boxing `Character`/`Integer`) — faster in practice, still O(n) time but with a better constant factor.

---

## 3. Frequency Map Patterns — the general technique

**Definition:** Building a `Map<T, Integer>` (or array) that counts occurrences of each element — the foundation for a huge family of problems: majority element, first non-repeating character, grouping, top-K frequent elements, and more.

### Pattern template

```java
Map<T, Integer> freq = new HashMap<>();
for (T item : items) {
    freq.merge(item, 1, Integer::sum);   // clean way to increment-or-initialize
}
```

**Interview tip:** `freq.merge(key, 1, Integer::sum)` is cleaner than the older idiom `freq.put(key, freq.getOrDefault(key, 0) + 1)` — both work, but merge is considered more idiomatic modern Java and worth using to show familiarity with the API.

### Example A — First Non-Repeating Character

```java
public int firstUniqChar(String s) {
    Map<Character, Integer> freq = new HashMap<>();
    for (char c : s.toCharArray()) {
        freq.merge(c, 1, Integer::sum);
    }
    for (int i = 0; i < s.length(); i++) {
        if (freq.get(s.charAt(i)) == 1) {
            return i;   // first character with count exactly 1
        }
    }
    return -1;
}
```

**Key idea:** Two passes — first builds the full frequency picture, second re-scans in original order to find the first one that qualifies. A single pass cannot work here because you do not know the total count of a character until you have seen the whole string.

### Example B — Group Anagrams

This extends the anagram-check idea:

```java
public List<List<String>> groupAnagrams(String[] strs) {
    Map<String, List<String>> groups = new HashMap<>();
    for (String s : strs) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        String key = new String(chars);   // sorted string acts as the signature for a group
        groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
    }
    return new ArrayList<>(groups.values());
}
```

**Key idea:** Anagrams share the same sorted form — use that sorted string as the "signature" for a group. `computeIfAbsent` avoids manually checking whether a key exists before adding to its list.

### Example C — Majority Element

The majority element appears more than `n/2` times:

```java
public int majorityElement(int[] nums) {
    Map<Integer, Integer> freq = new HashMap<>();
    for (int num : nums) {
        freq.merge(num, 1, Integer::sum);
        if (freq.get(num) > nums.length / 2) {
            return num;
        }
    }
    return -1;
}
```

**Good follow-up to mention:** This problem also has an O(1)-space solution, the Boyer-Moore Voting Algorithm. It is worth knowing even if the HashMap version is your primary answer, since interviewers sometimes push for the space-optimized version as a follow-up.

### Example D — Subarray Sum Equals K

Revisited from Topic 1: a HashMap of prefix-sum **frequencies**, using the same underlying pattern:

```java
Map<Integer, Integer> prefixCount = new HashMap<>();
prefixCount.put(0, 1);
```

**Connecting the dots:** This is the same frequency-map idea applied to prefix sums instead of raw values — showing that you recognize the pattern is reused across different problems, not memorized in isolation.

---

## 4. Why HashMap Gives O(1) Average Lookup

A HashMap uses a hash function to convert a key into an array index (bucket). Ideally, each key lands in its own bucket, so lookup, insert, and delete are O(1) on average.

**Worst-case caveat** (a nice thing to mention proactively): if many keys hash to the same bucket (collision), and Java resolves collisions via linked lists in that bucket, lookup degrades toward O(n) in pathological cases. Since Java 8, if a single bucket's list grows beyond a threshold (8 entries), Java converts it to a red-black tree, capping worst-case lookup at O(log n) instead of O(n) — a nice detail to drop if asked about HashMap internals.
