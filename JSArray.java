import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import javax.naming.SizeLimitExceededException;

public class JSArray<T extends Object> {

    private T[] elements;
    private final int MAX_CAPACITY = Integer.MAX_VALUE - 1;

    private class JSArrayIterator implements Iterator<T> {

        int index;

        public JSArrayIterator() {
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            return this.index < elements.length;
        }

        @Override
        public T next() {
            if (this.hasNext() && this.index < elements.length) {
                return elements[this.index++];
            }
            return null;
        }

    }

    private int getDimension() {
        int dimension = 0;

        Class cls = this.elements.getClass();

        while (cls.isArray()) {
            cls = cls.getComponentType();
            dimension++;
        }

        return dimension;
    }

    public JSArray(@SuppressWarnings("unchecked") T... array) throws SizeLimitExceededException {
        if (array.length > MAX_CAPACITY) {
            throw new SizeLimitExceededException("Cannot exceed max size: " + MAX_CAPACITY);
        }
        this.elements = array;
    }

    public static String[] from(String text) {
        return text.split("");
    }

    public static <T extends Object> T[] from(T[] array, Function<T, ?> mapper) {
        T[] out = Arrays.copyOf(array, array.length);
        for (int i = 0; i < array.length; i++) {
            out[i] = (T) mapper.apply(array[i]);
        }

        return out;
    }

    public T[] get() {
        return this.elements;
    }

    public T at(int index) {
        if (index >= this.elements.length) {
            throw new IndexOutOfBoundsException();
        }
        return this.elements[index];
    }

    private T[] concat(T[] array) {
        T[] out = Arrays.copyOf(this.elements, this.elements.length + array.length);

        for (int i = 0; i < array.length; i++) {
            out[i + this.elements.length] = (T) array[i];
        }

        return out;
    }

    public JSArray concat(JSArray array) {
        this.elements = this.concat((T[]) array.get());

        return this;
    }

    /*
     * console.log([1, 2, 3, 4, 5].copyWithin(0, 3));
     * // [4, 5, 3, 4, 5]
     * 
     * console.log([1, 2, 3, 4, 5].copyWithin(0, 3, 4));
     * // [4, 2, 3, 4, 5]
     */
    public JSArray copyWithin(int target, int start, int end) {
        if (target > end) {
            throw new IllegalArgumentException("target index must be lower than end index");
        }

        if (start > end || start == end) {
            throw new IllegalArgumentException("start index must be lower than end index");
        }

        ArrayDeque<T> range = new ArrayDeque<>();

        for (int i = 0; i < this.elements.length; i++) {
            if (i >= start && i < end) {
                range.add(this.elements[i]);
            }
        }

        int i = target;
        while (!range.isEmpty() && i < this.elements.length) {
            this.elements[i] = range.pop();
            i++;
        }

        return this;
    }

    public Iterator<T> entries() {
        return new JSArrayIterator();
    }

    public boolean every(BiFunction<T, Integer, Boolean> predicate) {
        boolean isValid = true;

        for (int i = 0; i < this.elements.length; i++) {
            isValid = predicate.apply(this.elements[i], i).booleanValue();
            if (!isValid) {
                return isValid;
            }
        }

        return isValid;
    }

    public JSArray fill(T value, int start, int end) {
        if (start >= end) {
            throw new IllegalArgumentException("start index must be lower than end index");
        }

        if (start >= this.elements.length || end > this.elements.length) {
            throw new IllegalArgumentException("indices lust be lower than array size");
        }

        for (int i = start; i < end; i++) {
            this.elements[i] = value;
        }

        return this;
    }

    public JSArray filter(BiPredicate<T, Integer> predicate) {
        ArrayList<T> filtered = new ArrayList<>();

        for (int i = 0; i < this.elements.length; i++) {
            if (predicate.test(this.elements[i], i)) {
                filtered.add(this.elements[i]);
            }
        }

        this.elements = (T[]) filtered.toArray(new Object[filtered.size()]);

        return this;
    }

    public T find(BiPredicate<T, Integer> predicate) {
        for (int i = 0; i < this.elements.length; i++) {
            if (predicate.test(this.elements[i], i)) {
                return this.elements[i];
            }
        }

        return null;
    }

    public int findIndex(BiPredicate<T, Integer> predicate) {
        for (int i = 0; i < this.elements.length; i++) {
            if (predicate.test(this.elements[i], i)) {
                return i;
            }
        }

        return -1;
    }

    public T findLast(BiPredicate<T, Integer> predicate) {
        for (int i = this.elements.length - 1; i >= 0; i--) {
            if (predicate.test(this.elements[i], i)) {
                return this.elements[i];
            }
        }

        return null;
    }

    public int findLastIndex(BiPredicate<T, Integer> predicate) {
        for (int i = this.elements.length - 1; i >= 0; i--) {
            if (predicate.test(this.elements[i], i)) {
                return i;
            }
        }

        return -1;
    }

    public JSArray flat() {
        int dimension = this.getDimension();

        if (dimension <= 1) {
            return this;
        }

        ArrayList<T> out = new ArrayList<>();

        for (int i = 0; i < this.elements.length; i++) {
            for (int j = 0; j < ((T[]) this.elements[i]).length; j++) {
                out.add(((T[]) this.elements[i])[j]);
            }
        }

        this.elements = (T[]) out.toArray(new Object[out.size()]);

        return this;
    }

    /*
     * public JSArray flatMap(BiFunction<T, Integer, ?> mapper) {
     * return this.flat().map(mapper);
     * }
     */

    public void forEach(BiConsumer<T, Integer> function) {
        for (int i = 0; i < this.elements.length; i++) {
            function.accept(this.elements[i], i);
        }
    }

    public boolean includes(T t) {
        return this.find((e, i) -> e.equals(t)) != null;
    }

    public int indexOf(T t) {
        for (int i = 0; i < this.elements.length; i++) {
            if (this.elements[i].equals(t)) {
                return i;
            }
        }
        
        return -1;
    }

    public JSArray push(T item) throws SizeLimitExceededException {
        for (int i = 0; i < this.elements.length; i++) {
            if (elements[i] == null) {
                elements[i] = item;
                return this;
            }
        }

        throw new SizeLimitExceededException("Cannot push to array");
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("[");

        for (int i = 0; i < this.elements.length; i++) {
            builder.append(this.elements[i]);
            if (i < this.elements.length - 1) {
                builder.append(", ");
            }
        }

        builder.append("]");

        return builder.toString();
    }

}