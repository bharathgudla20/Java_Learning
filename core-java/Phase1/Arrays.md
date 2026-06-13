# Arrays

## Declare & Initialize
```java
int[] arr = new int[5];              // size 5, default values 0
int[] arr = {10, 20, 30, 40, 50};    // inline init
```

## Key facts
- Zero-indexed — first element is `arr[0]`
- Fixed size — can't grow after creation
- `arr.length` gives size (not a method, it's a property)

## Common operations
```java
Arrays.sort(arr);                     // sort in place
Arrays.toString(arr);                 // print nicely
int[] copy = Arrays.copyOf(arr, 3);   // copy first 3 elements
```

## 2D Array
```java
int[][] matrix = new int[3][4];       // 3 rows, 4 cols
matrix[0][1] = 5;                     // row 0, col 1
// iterate:
for (int i = 0; i < matrix.length; i++)
    for (int j = 0; j < matrix[i].length; j++)
```

> Interview: `ArrayIndexOutOfBoundsException` = accessing index ≥ length. Most common array bug.
