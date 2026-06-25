# Maximum Number of Balloons

## Problem Statement

Given a string `text`, you want to use the characters of `text` to form as many instances of the word **"balloon"** as possible.

You can use each character in `text` **at most once**. Return the maximum number of instances that can be formed.

### Examples

**Example 1:**
```
Input: text = "nlaebolko"
Output: 1
```

**Example 2:**
```
Input: text = "loonbalxballpoon"
Output: 2
```

**Example 3:**
```
Input: text = "leetcode"
Output: 0
```

---

## Java Solution

```java
class Solution {
    public int maxNumberOfBalloons(String text) {
        HashMap<Character,Integer>mp=new HashMap<>();
        for(char ch:text.toCharArray())
        {
            if("balloon".indexOf(ch)!=-1)
            {
                mp.put(ch,mp.getOrDefault(ch,0)+1);
            }
        }
        List<Character>li1=new ArrayList<>(mp.keySet());
        for(int i=0;i<mp.size();i++)
        {
            char key=li1.get(i);
            if(key=='l' || key=='o')
            {
                mp.put(key,mp.get(key)/2);
            }
        }
        List<Integer>li=new ArrayList<>(mp.values());
        if(li.size()!=5)
        return 0;
        int mini=li.get(0);
        for(int x:li)
        {
            mini=Math.min(mini,x);
        }
        
        return mini;
        
    }
}
```
