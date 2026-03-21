import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.naming.SizeLimitExceededException;

public class Main {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws SizeLimitExceededException {
        JSArray<String> array = new JSArray<>("T", "E", "S", "T");
        JSArray<String> array2 = new JSArray<>("A", "B", "C");
        JSArray<String> array3 = JSArray.from("TEXT");
        JSArray<String> array4 = JSArray.from(array3, (x) -> {
            return ((String) x).toLowerCase();
        });
        JSArray<String[]> array5 = new JSArray<>(array3.toPrimitiveArray(), array4.toPrimitiveArray());

        System.out.println("First array: " + array);
        System.out.println("Second array: " + array2);
        System.out.println("Thrid array: " + array3);
        System.out.println("Fourth array: " + array4);

        JSArray concatenated = array2.concat(array);
        System.out.println("Concatenate second array with first: " + concatenated);

        JSArray copy = concatenated.copyWithin(1, 3, 6);
        System.out.println("Copy values within concatenated array: " + copy);

        Iterator it = array2.entries();
        System.out.println("Iterate over second array");
        while (it.hasNext()) {
            System.out.println("Element: " + it.next());
        }

        System.out.println("Each value in first array is a 'T': " + array.every((elt, i) -> {
            return elt.equals("T");
        }));

        JSArray filled = concatenated.fill("0", 2, 4);
        System.out.println("Fill concatenated array with '0' at index 2 to index 3: " + filled);

        JSArray filtered = array2.filter((elt, i) -> {
            return i % 2 == 0;
        });
        System.out.println("Filter second array to retrieve only values with even index: " + filtered);

        String elt = array2.find((e, i) -> {
            return i == 2;
        });
        System.out.println("Find element at index 2 in second array: " + elt);

        int index = array.findIndex((e, i) -> {
            return e.equals("T");
        });
        System.out.println("Find index of element 'T' in first array: " + index);

        String lastElt = array2.findLast((e, i) -> {
            return i > 0;
        });
        System.out.println("Find last element which its index is greater than 0 in second array: " + lastElt);

        int lastIndex = array.findLastIndex((e, i) -> {
            return e.equals("T");
        });
        System.out.println("Find last index of 'T' in first array: " + lastIndex);

        JSArray flatted = array5.flat();
        System.out.println("Flat fifth array: " + flatted);

        System.out.println("Lopping through second array to print each item with its matching index");
        array2.forEach((e, i) -> System.out.println("[" + e + ", " + i + "]"));

        boolean included = array.includes("0");
        System.out.println("First array include '0': " + included);

        int eltIndex = array2.indexOf("0");
        System.out.println("Find index of '0' in second array: " + eltIndex);

        String defaultJoined = array2.join(null);
        System.out.println("Join items with default separator in second array: " + defaultJoined);

        String joined = array2.join("-");
        System.out.println("Join items with custom separator in second array: " + joined);

        int[] keys = array.keys();
        System.out.println("Keys from first array");
        for (int key : keys) {
            System.out.println(key);
        }

        int lastIdx = array2.lastIndexOf("A", 2);
        System.out.println("Get last index of 'A' of second array: " + lastIdx);

        JSArray mapped = array2.map((e, i) -> {
            return i;
        });
        System.out.println("Map second array to have indices instead of values: " + mapped);

        String popped = array.pop();
        System.out.println("Pop element from first array: " + popped + ", first array: " + array);

        int length = array.push(new String[] { "H", "E", "L", "L", "O" });
        System.out.println("Push elements into first array, new array: " + array + " (" + length + ")");

        JSArray reduced = array.reduce((acc, curr, idx) -> {
            ArrayList<String> list = new ArrayList<>(Arrays.asList(acc));
            if (idx % 2 == 0) {
                list.add(curr);
            }
            return list.toArray(new String[0]);
        }, new String[0]);
        System.out.println("Reduce first array to contains only element at which index is even: " + reduced);

        JSArray<Double> array6 = new JSArray<>(1.0, 2.0, 3.0, 4.0, 5.0);
        double reducedValue = array6.reduce((acc, curr, idx) -> {
            return acc + curr;
        }, 0);

        System.out.println("Sum all numbers of the sixth array: " + reducedValue);

        JSArray reversed = array6.reverse();
        System.out.println("Reverse sixth array: " + reversed);

        double shifted = array6.shift();
        System.out.println("Shift head of the sixth array: " + shifted + ", sixth array: " + array6);

        JSArray sliced = array6.slice(1, 3);
        System.out.println("Slice sixth array from index 1 to index 2: " + sliced);

        System.out.println("Sixth array contains at least one even value: " + array6.some((e, i) -> {
            return e % 2 == 0;
        }));

        JSArray<Double> array7 = new JSArray<>(1.0, 2.0, 3.0, 4.0, 5.0);

        JSArray<Double> sorted = array7.sort((a, b) -> {
            return (int) (b - a);
        });
        System.out.println("Sorted seventh array descendant: " + sorted);

        JSArray<Double> spliced = sorted.splice(1, 2, new Double[0]);
        System.out.println("Removed items from sorted array: " + spliced);
        System.out.println("Sorted array now: " + sorted);

        sorted.splice(1, 1, new Double[] {0.0, 2.0, 6.5, 4.2, 9.0, 7.6});
        System.out.println("Removed 1 item from sorted array and replaced it with new values: " + sorted);
    }
}
