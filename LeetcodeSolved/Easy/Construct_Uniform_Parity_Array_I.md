# Construct Uniform Parity Array I

You are given an array `nums1` of `n` **distinct** integers.

You want to construct another array `nums2` of length `n` such that the elements in `nums2` are either **all odd or all even**.

For each index `i`, you must choose exactly one of the following (in any order):

- `nums2[i] = nums1[i]`
- `nums2[i] = nums1[i] - nums1[j]`, for an index `j != i`

Return `true` if it is possible to construct such an array, otherwise, return `false`.

---

## Example 1

**Input:** `nums1 = [2,3]`

**Output:** `true`

**Explanation:**

- Choose `nums2[0] = nums1[0] - nums1[1] = 2 - 3 = -1`.
- Choose `nums2[1] = nums1[1] = 3`.
- `nums2 = [-1, 3]`, and both elements are odd. Thus, the answer is `true`.

## Example 2

**Input:** `nums1 = [4,6]`

**Output:** `true`

**Explanation:**

- Choose `nums2[0] = nums1[0] = 4`.
- Choose `nums2[1] = nums1[1] = 6`.
- `nums2 = [4, 6]`, and all elements are even. Thus, the answer is `true`.

---

## Solution

```java
return true;
```

Since every time we only need to consider the parity of each element. Equal parities produce an even difference. Different parities produce an odd difference:

- even - even = even
- odd - odd = even
- even - odd = odd
- odd - even = odd

```java
class Solution {
    public boolean uniformArray(int[] nums1) {
        int ecnt=0;
        int ocnt=0;
        int n=nums1.length;
        for(int i=0;i<n;i++)
        {
            if(nums1[i]%2==0)
                {
                    ecnt++;
                }
                else
                {
                    ocnt++;
                }
                int eflag=0;
                int oflag=0;
            for(int j=0;j<n && i!=j;j++)
            {
               if(Math.abs(nums1[i]-nums1[j])%2==0 )
                {
                    if(eflag==0){
                    ecnt++;
                    eflag=1;
                    }
                }
                else
                {
                    if(oflag==0)
                    {
                        oflag=1;
                        ocnt++;
                    }
                }
            }
            System.out.println(ecnt+" "+ocnt);
        }
        if(ecnt>=n || ocnt>=n)
        return true;
        else
        return false;

    }
}
```
