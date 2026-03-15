import java.util.Iterator;

import javax.naming.SizeLimitExceededException;

public class Main {

    public static void main(String[] args) throws SizeLimitExceededException {
        JSArray<String> array = new JSArray<>("T", "E", "S", "T");
        JSArray<String> array2 = new JSArray<>("A", "B", "C");
        String[] array3 = JSArray.from("TEXT");
        String[] array4 = JSArray.from(array3, (x) -> {
            return x.toLowerCase();
        });
        JSArray<String[]> array5 = new JSArray<>(array3, array4);

        System.out.println("First array: " + array);
        System.out.println("Second array: " + array2);

        System.out.println("Thrid array: ");
        for (String item : array3) {
            System.out.println(item);
        }

        System.out.println("Fourth array: ");
        for (String item : array4) {
            System.out.println(item);
        }

        array2.concat(array);
        System.out.println("Concatenate second array with first: " + array2);

        array2.copyWithin(1, 3, 6);
        System.out.println("Copy values within second array: " + array2);

        Iterator it = array2.entries();
        System.out.println("Iterate over second array");
        while (it.hasNext()) {
            System.out.println("Element: " + it.next());
        }

        System.out.println("Each value in first array is a 'T': " + array.every((elt, i) -> {
            return elt.equals("T");
        }));

        array2.fill("0", 2, 4);
        System.out.println("Fill second array with '0' at index 2 to index 3: " + array2);

        array2.filter((elt, i) -> {
            return i % 2 == 0;
        });
        System.out.println("Filter second array to retrieve only values with even index: " + array2);

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

        array5.flat();
        System.out.println("Flat fifth array: " + array5);

        System.out.println("Lopping through second array to print each item with its matching index");
        array2.forEach((e, i) -> System.out.println("[" + e + ", " + i + "]"));

        boolean included = array.includes("0");
        System.out.println("First array include '0': " + included);

        int eltIndex = array2.indexOf("0");
        System.out.println("Find index of '0' in second array: " + eltIndex);
    }
}
