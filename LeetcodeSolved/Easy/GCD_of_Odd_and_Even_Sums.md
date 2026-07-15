# GCD of Odd and Even Sums

You are given an integer `n`. Your task is to compute the GCD (greatest common divisor) of two values:

- `sumOdd`: the sum of the smallest `n` positive odd numbers
- `sumEven`: the sum of the smallest `n` positive even numbers

Return the GCD of `sumOdd` and `sumEven`.

## Example 1

**Input:** `n = 4`

**Output:** `4`

**Explanation:**
- Sum of the first 4 odd numbers: `1 + 3 + 5 + 7 = 16`
- Sum of the first 4 even numbers: `2 + 4 + 6 + 8 = 20`
- `GCD(16, 20) = 4`

## Example 2

**Input:** `n = 5`

**Output:** `5`

**Explanation:**
- Sum of the first 5 odd numbers: `1 + 3 + 5 + 7 + 9 = 25`
- Sum of the first 5 even numbers: `2 + 4 + 6 + 8 + 10 = 30`
- `GCD(25, 30) = 5`

---

## Solution 1: Direct Computation

```java
class Solution {
    int gcd(int a, int b) {
        while (a != 0) {
            int rem = b % a;
            b = a;
            a = rem;
        }
        return b;
    }

    public int gcdOfOddEvenSums(int n) {
        int sumOdd = 0;
        int sumEven = 0;
        int num1 = 1;
        int n1 = n;
        int n2 = n;

        while (n1 != 0) {
            if (num1 % 2 != 0) {
                sumOdd += num1;
                n1--;
            }
            num1++;
        }

        num1 = 2;
        while (n2 != 0) {
            if (num1 % 2 == 0) {
                sumEven += num1;
                n2--;
            }
            num1++;
        }

        System.out.println(sumOdd + " " + sumEven);
        return gcd(sumOdd, sumEven);
    }
}
```

### Explanation

This solution directly calculates:
- the sum of the first `n` odd numbers
- the sum of the first `n` even numbers

Then it finds their GCD using the Euclidean algorithm.

---

## Solution 2: Optimized Math Observation

### Intuition

The sum of the first `n` odd numbers is a perfect square:

$$
1 + 3 + 5 + \dots + (2n-1) = n^2
$$

The sum of the first `n` even numbers is:

$$
2 + 4 + 6 + \dots + 2n = n(n+1)
$$

We need:

$$
\gcd(n^2,\ n(n+1))
$$

Factor out `n`:

$$
\gcd(n^2, n(n+1)) = n \cdot \gcd(n, n+1)
$$

Since `n` and `n+1` are consecutive integers, they are always coprime:

$$
\gcd(n, n+1) = 1
$$

So the answer is simply:

$$
\boxed{n}
$$

### Code

```cpp
class Solution {
public:
    int gcdOfOddEvenSums(int n) {
        return n;
    }
};
```

### Why this works

No actual GCD computation or loop is needed. The answer is always `n`.

---

## Final Conclusion

The GCD of the sum of the first `n` odd numbers and the sum of the first `n` even numbers is always:

$$
\boxed{n}
$$
