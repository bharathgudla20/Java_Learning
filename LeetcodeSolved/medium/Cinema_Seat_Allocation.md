 # 1386. Cinema Seat Allocation

A cinema has `n` rows of seats, numbered from 1 to `n`. Each row has 10 seats, numbered from 1 to 10.

You are given a 2D integer array `reservedSeats`, where `reservedSeats[i] = [rowi, seati]` means that seat `seati` in row `rowi` is already reserved.

A four-person group must be assigned to four seats in the **same** row. The group can be seated in one of the following seat blocks:

- seats `2, 3, 4, 5`
- seats `4, 5, 6, 7`
- seats `6, 7, 8, 9`

A block can be used only if **none of its seats are reserved**. Each seat can be assigned to **at most** one group.

Return an integer denoting the **maximum** number of four-person groups that can be assigned.

## Example 1

```text
Input: n = 3, reservedSeats = [[1,2],[1,3],[1,8],[2,6],[3,1],[3,10]]
Output: 4
```

## Example 2

```text
Input: n = 2, reservedSeats = [[2,1],[1,8],[2,6]]
Output: 2
```

## Example 3

```text
Input: n = 4, reservedSeats = [[4,3],[1,4],[4,6],[1,7]]
Output: 4
```

Intuition
Each row has 10 seats, but a family of 4 can only occupy one of these three groups:
[2, 3, 4, 5], [4, 5, 6, 7], [6, 7, 8, 9]
If a row has no reserved seats, we can directly place 2 families in that row using seats 2-5 and 6-9.
For each such row, we check whether each of the three possible groups is available. When we choose a group, we mark its overlapping seats as occupied so that another overlapping group cannot be selected.

Approach
Use a map to group all reserved seats according to their row number.

Suppose there are k rows containing at least one reserved seat. Then the remaining n-k rows are completely empty and can accommodate 2 families each.

Therefore, initially:

ans = (n - k) * 2

For every row containing reserved seats, store its reserved seat numbers in a temporary map.

Check the three possible family arrangements:
If seats 2, 3, 4, 5 are free, place a family there and mark seats 4 and 5 as occupied because they overlap with the middle group.
If seats 4, 5, 6, 7 are free, place a family there and mark seats 6 and 7 as occupied because they overlap with the right group.
If seats 6, 7, 8, 9 are free, place a family there.

Return the total number of families.
```java
class Solution {
    public int maxNumberOfFamilies(int n, int[][] reservedSeats) {
        HashMap<Integer,List<Integer>>mp=new HashMap<>();
        int ans=0;
        for(int i=1;i<=n;i++)
        {
            mp.put(i,new ArrayList<>());
        }
        for(int arr[] :reservedSeats)
        {
            mp.get(arr[0]).add(arr[1]);
        }
        for(int x:mp.keySet())
        {
            HashMap<Integer,Integer>mp2=new HashMap<>();
            for(int y:mp.get(x))
            {
                mp2.put(y,mp2.getOrDefault(y,0)+1);
            }
            if(!mp2.containsKey(2)&&!mp2.containsKey(3)&&!mp2.containsKey(4)&&!mp2.containsKey(5))
            {
                ans++;
                mp2.put(4,1);
                mp2.put(5,1);

            }
            if(!mp2.containsKey(4)&&!mp2.containsKey(5)&&!mp2.containsKey(6)&&!mp2.containsKey(7))
            {
                ans++;
                mp2.put(6,1);
                mp2.put(7,1);

            }
            if(!mp2.containsKey(6)&&!mp2.containsKey(7)&&!mp2.containsKey(8)&&!mp2.containsKey(9))
            {
                ans++;
            }
        }
        return ans;
    }
}
```
