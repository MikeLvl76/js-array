# js-array

## Description

An implementation of JavaScript [Array](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array) object in Java.

## How to use

```java
public class Main {
    public static void main(String[] args) {
        JSArray<Integer> array = new JSArray<>(1, 2, 3, 4, 5);

        // Expected output: [1, 2, 3, 4, 5]
        System.out.println(array);
    }
}
```

***Note**: An exception as `SizeLimitExceededException` must be handled and if a linter is used it will return some warnings: this is due to generic type used by the `JSArray` class. With raw types such as `int`, `double` and others, use their object version like `Integer`, `Double`, etc.*

## Examples

1. Converting a String into a JSArray:

```java
public class Main {
    public static void main(String[] args) {
        JSArray<String> array = JSArray.from("Hello");

        // Expected output: [H, e, l, l, o]
        System.out.println(array);
    }
}
```

2. Concatenate two arrays:

```java
public class Main {
    public static void main(String[] args) {
        JSArray<String> array = JSArray.from("Hello");
        JSArray<String> array2 = JSArray.from("World");

        JSArray<String> result = array.concat(array2);

        // Expected output: [H, e, l, l, o, W, o, r, l, d]
        System.out.println(result);
    }
}
```

3. Map an array of double values to have each value multiplied by itself:

```java
public class Main {
    public static void main(String[] args) {
        JSArray<Double> array = new JSArray<>(2.0, 4.0, 6.0, 8.0);
        JSArray<Double> newArray = array.map((d, i) -> d * d);

        // Expected output: [4.0, 16.0, 36.0, 64.0]
        System.out.println(newArray);
    }
}
```

4. Flatten JSArray that is multi-dimensionnal:

***Note**: `flat()` method will return an array with the same type as the input. This is not intended and could be fixed but it returns the expected result and assigning it to a new array will operate a weird cast that works.*

```java
public class Main {
    public static void main(String[] args) {
        Integer[][][] a = {{{1, 2}, {3, 4}}, {{5, 6}, {7, 8}}};
        JSArray<Integer[][]> array = new JSArray(a);

        // The argument represents the depth of the array.
        // A depth of 0 means the array is already flat.
        // Knowing the array’s dimension helps determine the correct depth value.
        // Typically, depth can be set to (dimension - 1) to fully flatten the array.
        JSArray newArray = array.flat(2);

        // Expected output: [1, 2, 3, 4, 5, 6, 7, 8]
        System.out.println(newArray);
    }
}
```

5. Iterating over an array (two ways):

```java
public class Main {
    public static void main(String[] args) {
        JSArray<Integer> array = new JSArray(1, 2, 3, 4, 5, 6, 7, 8);

        /* 
        * Expected output:
        *
        * 1
        * 2
        * 3
        * 4
        * 5
        * 6
        * 7
        * 8
        *
        */
        Iterator it = array.values();
        while (it.hasNext()) {
            System.out.println(it.next());
        }

        /* 
        * Expected output:
        *
        * 1
        * 2
        * 3
        * 4
        * 5
        * 6
        * 7
        * 8
        *
        */
        for (int i = 0; i < array.size(); i++) {
            System.out.println(array.at(i));
        }
    }
}
```

## Improvements

- Optimize code
- Handle negative index like JS array does
- Fix weird type casting
- Write unit tests to strongly check code
- Maybe write documentation