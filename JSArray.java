import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiFunction;
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

    public JSArray concat(JSArray array) {
        T[] out = Arrays.copyOf(this.elements, this.elements.length + array.get().length);

        for (int i = 0; i < array.get().length; i++) {
            out[i + this.elements.length] = (T) array.at(i);
        }

        this.elements = out;

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

    public JSArray filter(BiFunction<T, Integer, Boolean> predicate) {
        ArrayList<T> filtered = new ArrayList<>();

        for (int i = 0; i < this.elements.length; i++) {
            if (predicate.apply(this.elements[i], i).booleanValue()) {
                filtered.add(this.elements[i]);
            }
        }


        this.elements = (T[]) filtered.toArray(new Object[filtered.size()]);

        return this;
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