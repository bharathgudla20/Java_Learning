# Arrays

## What an array is
A fixed-size row of boxes, all the same type, indexed from 0. Think of it like school lockers — numbered, same size, fixed count.

## The 3 things to always remember
- Index starts at `0`; the last index is always `length - 1`
- Size is fixed — you can't add or remove elements after creation
- `.length` has no brackets — it's a property, not a method

## Declare & initialize
```java
int[] arr = new int[5];              // size 5, default values 0
int[] arr = {10, 20, 30, 40, 50};    // inline init
```

## Two ways to loop
- Normal `for` loop → when you need the index or want to modify elements
- Enhanced `for` loop → when you just need the values, cleaner to read

```java
for (int i = 0; i < arr.length; i++) {
    arr[i] = arr[i] * 2;            // use index
}

for (int value : arr) {
    System.out.println(value);      // just read values
}
```

## 2D arrays
Think of a 2D array as a table. Use `matrix[row][col]` — row first, column second.

```java
int[][] matrix = new int[3][4];       // 3 rows, 4 cols
matrix[0][1] = 5;                     // row 0, col 1
```

- `matrix.length` gives number of rows
- `matrix[0].length` gives number of columns

```java
for (int i = 0; i < matrix.length; i++) {
    for (int j = 0; j < matrix[i].length; j++) {
        System.out.print(matrix[i][j] + " ");
    }
    System.out.println();
}
```

## Common runtime error
```java
int[] arr = {10, 20, 30};   // indexes: 0, 1, 2
arr[3];   // 💥 ArrayIndexOutOfBoundsException — no index 3!
```

> Interview tip: `ArrayIndexOutOfBoundsException` happens when you access an index ≥ length. It is the most common array bug.
