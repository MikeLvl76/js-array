import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import javax.naming.SizeLimitExceededException;

public class JSArray<T> {

    private T[] elements;
    private final int MAX_CAPACITY = Integer.MAX_VALUE - 1;
    private int length;

    private class JSArrayIterator<U> implements Iterator<U> {

        private int index;
        private final Function<Integer, U> mapper;

        public JSArrayIterator(Function<Integer, U> mapper) {
            this.index = 0;
            this.mapper = mapper;
        }

        @Override
        public boolean hasNext() {
            return this.index < length;
        }

        @Override
        public U next() {
            if (!this.hasNext() || this.index >= length) {
                return null;

            }
            return mapper.apply(index++);
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

    @SuppressWarnings("unchecked")
    public JSArray(int _length) throws SizeLimitExceededException {
        if (length > MAX_CAPACITY) {
            throw new SizeLimitExceededException("Cannot exceed max size: " + MAX_CAPACITY);
        }
        this.length = _length;
        this.elements = (T[]) new Object[_length];
    }

    public JSArray(@SuppressWarnings("unchecked") T... values) throws SizeLimitExceededException {
        if (values.length > MAX_CAPACITY) {
            throw new SizeLimitExceededException("Cannot exceed max size: " + MAX_CAPACITY);
        }
        this.elements = values;
        this.length = this.elements.length;
    }

    public static JSArray from(String text) throws SizeLimitExceededException {
        return new JSArray<>(text.split(""));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Object> JSArray from(JSArray array, Function<T, ?> mapper)
            throws SizeLimitExceededException {
        T[] out = (T[]) Arrays.copyOf(array.toPrimitiveArray(), array.length);
        for (int i = 0; i < array.length; i++) {
            out[i] = (T) mapper.apply((T) array.toPrimitiveArray()[i]);
        }

        return new JSArray<>(out);
    }

    public T[] toPrimitiveArray() {
        return this.elements;
    }

    public T at(int index) {
        if (index >= this.length) {
            throw new IndexOutOfBoundsException();
        }
        return this.elements[index];
    }

    private T[] concat(T[] array) {
        T[] out = Arrays.copyOf(this.elements, this.length + array.length);

        for (int i = 0; i < array.length; i++) {
            out[i + this.length] = (T) array[i];
        }

        return out;
    }

    @SuppressWarnings("unchecked")
    public JSArray concat(JSArray array) throws SizeLimitExceededException {
        return new JSArray<>(this.concat((T[]) array.toPrimitiveArray()));
    }

    /*
     * console.log([1, 2, 3, 4, 5].copyWithin(0, 3));
     * // [4, 5, 3, 4, 5]
     * 
     * console.log([1, 2, 3, 4, 5].copyWithin(0, 3, 4));
     * // [4, 2, 3, 4, 5]
     */
    public JSArray copyWithin(int target, int start, int end) throws SizeLimitExceededException {
        if (target > end) {
            throw new IllegalArgumentException("target index must be lower than end index");
        }

        if (start > end || start == end) {
            throw new IllegalArgumentException("start index must be lower than end index");
        }

        ArrayDeque<T> range = new ArrayDeque<>();
        T[] out = Arrays.copyOf(this.elements, this.length);

        for (int i = 0; i < out.length; i++) {
            if (i >= start && i < end) {
                range.add(out[i]);
            }
        }

        int i = target;
        while (!range.isEmpty() && i < out.length) {
            out[i] = range.pop();
            i++;
        }

        return new JSArray<>(out);
    }

    public Iterator<Entry<Integer, T>> entries() {
        return new JSArrayIterator<Entry<Integer, T>>(i -> Map.entry(i, this.elements[i]));
    }

    public boolean every(BiFunction<T, Integer, Boolean> predicate) {
        boolean isValid = true;

        for (int i = 0; i < this.length; i++) {
            isValid = predicate.apply(this.elements[i], i).booleanValue();
            if (!isValid) {
                return isValid;
            }
        }

        return isValid;
    }

    public JSArray fill(T value, int start, int end) throws SizeLimitExceededException {
        if (start >= end) {
            throw new IllegalArgumentException("start index must be lower than end index");
        }

        if (start >= this.length || end > this.length) {
            throw new IllegalArgumentException("indices lust be lower than array size");
        }

        T[] out = Arrays.copyOf(this.elements, this.length);

        for (int i = start; i < end; i++) {
            out[i] = value;
        }

        return new JSArray<>(out);
    }

    public JSArray filter(BiPredicate<T, Integer> predicate) throws SizeLimitExceededException {
        ArrayList<T> filtered = new ArrayList<>();

        for (int i = 0; i < this.length; i++) {
            if (predicate.test(this.elements[i], i)) {
                filtered.add(this.elements[i]);
            }
        }

        @SuppressWarnings("unchecked")
        T[] result = (T[]) filtered.toArray(new Object[filtered.size()]);

        return new JSArray<>(result);
    }

    public T find(BiPredicate<T, Integer> predicate) {
        for (int i = 0; i < this.length; i++) {
            if (predicate.test(this.elements[i], i)) {
                return this.elements[i];
            }
        }

        return null;
    }

    public int findIndex(BiPredicate<T, Integer> predicate) {
        for (int i = 0; i < this.length; i++) {
            if (predicate.test(this.elements[i], i)) {
                return i;
            }
        }

        return -1;
    }

    public T findLast(BiPredicate<T, Integer> predicate) {
        for (int i = this.length - 1; i >= 0; i--) {
            if (predicate.test(this.elements[i], i)) {
                return this.elements[i];
            }
        }

        return null;
    }

    public int findLastIndex(BiPredicate<T, Integer> predicate) {
        for (int i = this.length - 1; i >= 0; i--) {
            if (predicate.test(this.elements[i], i)) {
                return i;
            }
        }

        return -1;
    }

    // TODO: optimize this and include depth
    @SuppressWarnings("unchecked")
    public JSArray flat() throws SizeLimitExceededException {
        int dimension = this.getDimension();

        if (dimension <= 1) {
            return this;
        }

        ArrayList<T> out = new ArrayList<>();

        for (int i = 0; i < this.length; i++) {
            for (int j = 0; j < ((T[]) this.elements[i]).length; j++) {
                out.add(((T[]) this.elements[i])[j]);
            }
        }

        T[] result = (T[]) out.toArray(new Object[out.size()]);

        return new JSArray<>(result);
    }

    @SuppressWarnings("unchecked")
    public <U extends Object> JSArray flatMap(BiFunction<T, Integer, U> mapper) throws SizeLimitExceededException {
        return new JSArray<>(this.elements).flat().map(mapper);
    }

    public void forEach(BiConsumer<T, Integer> function) {
        for (int i = 0; i < this.length; i++) {
            function.accept(this.elements[i], i);
        }
    }

    public boolean includes(T t) {
        return this.find((e, i) -> e.equals(t)) != null;
    }

    public int indexOf(T t) {
        for (int i = 0; i < this.length; i++) {
            if (this.elements[i].equals(t)) {
                return i;
            }
        }

        return -1;
    }

    public String join(String defaultSeparator) {
        String sep = ",";

        if (defaultSeparator != null) {
            sep = defaultSeparator;
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < this.length; i++) {
            builder.append(this.elements[i]);
            if (i < this.length - 1) {
                builder.append(sep);
            }
        }

        return builder.toString();
    }

    public int[] keys() {
        int[] keys = new int[this.length];

        for (int i = 0; i < this.length; i++) {
            keys[i] = i;
        }

        return keys;
    }

    public int lastIndexOf(T t, int fromIndex) {
        if (fromIndex < 0 || fromIndex >= this.length) {
            throw new IllegalArgumentException("index must be between 0 and array length - 1");
        }

        for (int i = fromIndex; i >= 0; i--) {
            if (this.elements[i].equals(t)) {
                return i;
            }
        }

        return -1;
    }

    @SuppressWarnings("unchecked")
    public <U extends Object> JSArray map(BiFunction<T, Integer, U> mapper) throws SizeLimitExceededException {
        U[] out = (U[]) new Object[this.length];

        for (int i = 0; i < this.length; i++) {
            out[i] = mapper.apply(this.elements[i], i);
        }

        return new JSArray<>((T[]) out);
    }

    public T pop() {
        T popped = null;

        if (this.length == 0) {
            return popped;
        }

        @SuppressWarnings("unchecked")
        T[] array = (T[]) new Object[this.length - 1];

        for (int i = 0; i < this.length; i++) {
            if (i == this.length - 1) {
                popped = this.elements[i];
                break;
            }
            array[i] = this.elements[i];

        }

        this.elements = array;
        this.length--;

        return popped;
    }

    public int push(@SuppressWarnings("unchecked") T... items) throws SizeLimitExceededException {
        if (items.length > MAX_CAPACITY || this.length + items.length > MAX_CAPACITY) {
            throw new SizeLimitExceededException("Cannot push to array, size exceeded");
        }

        this.elements = this.concat(items);
        this.length = this.elements.length;
        return this.length;
    }

    @SuppressWarnings("unchecked")
    public <U extends Object> JSArray reduce(TriFunction<U[], T, Integer, U[]> reducer, U[] initialValue)
            throws SizeLimitExceededException {
        U[] values = initialValue;

        for (int i = 0; i < this.length; i++) {
            values = reducer.apply(values, this.elements[i], i);
        }

        return new JSArray<>((T[]) values);
    }

    public double reduce(TriFunction<Double, T, Integer, Double> reducer, double initialValue) {
        double value = initialValue;
        for (int i = 0; i < this.length; i++) {
            value = reducer.apply(value, this.elements[i], i);
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    public <U extends Object> JSArray reduceRight(TriFunction<U[], T, Integer, U[]> reducer, U[] initialValue)
            throws SizeLimitExceededException {
        U[] values = initialValue;

        for (int i = this.length - 1; i >= 0; i--) {
            values = reducer.apply(values, this.elements[i], i);
        }

        return new JSArray<>((T[]) values);
    }

    public double reduceRight(TriFunction<Double, T, Integer, Double> reducer, double initialValue) {
        double value = initialValue;
        for (int i = this.length - 1; i >= 0; i--) {
            value = reducer.apply(value, this.elements[i], i);
        }

        return value;
    }

    public JSArray reverse() throws SizeLimitExceededException {
        @SuppressWarnings("unchecked")
        T[] reversed = (T[]) new Object[this.length];

        for (int i = 0; i < this.length; i++) {
            reversed[i] = this.elements[this.length - 1 - i];
        }

        return new JSArray<>(reversed);
    }

    public T shift() {
        T shifted = null;

        if (this.length == 0) {
            return shifted;
        }

        @SuppressWarnings("unchecked")
        T[] out = (T[]) new Object[this.length - 1];

        shifted = this.elements[0];
        for (int i = 1; i < this.length; i++) {
            out[i - 1] = this.elements[i];
        }

        this.elements = out;
        this.length--;

        return shifted;
    }

    public int size() {
        return this.length;
    }

    public JSArray slice(int start, int end) throws SizeLimitExceededException {
        if (start >= end) {
            throw new IllegalArgumentException("start index must be lower than end index");
        }

        ArrayList<T> out = new ArrayList<>();

        for (int i = start; i < end; i++) {
            out.add(this.elements[i]);
        }

        @SuppressWarnings("unchecked")
        T[] result = (T[]) out.toArray(new Object[out.size()]);

        return new JSArray<>(result);
    }

    public boolean some(BiFunction<T, Integer, Boolean> predicate) {
        boolean isValid = false;

        for (int i = 0; i < this.length; i++) {
            isValid = predicate.apply(this.elements[i], i).booleanValue();
            if (isValid) {
                return isValid;
            }
        }

        return isValid;
    }

    public JSArray<T> sort(BiFunction<T, T, Integer> comparator) throws SizeLimitExceededException {
        T[] out = Arrays.copyOf(this.elements, this.length);

        for (int i = 0; i < out.length - 1; i++) {
            int minIndex = i;

            for (int j = i + 1; j < out.length; j++) {
                if (comparator.apply(out[minIndex], out[j]) > 0) {
                    minIndex = j;
                }
            }

            if (minIndex != i) {
                T temp = out[i];
                out[i] = out[minIndex];
                out[minIndex] = temp;
            }
        }

        return new JSArray<>(out);
    }

    @SuppressWarnings("unchecked")
    public JSArray splice(int start, int deleteCount, T... items) throws SizeLimitExceededException {
        if (this.length - deleteCount + items.length > MAX_CAPACITY) {
            throw new SizeLimitExceededException("too much values to insert");
        }

        if (this.length < deleteCount) {
            throw new IllegalArgumentException("cannot delete more values than array can contain");
        }

        if (start >= this.length) {
            throw new IllegalArgumentException("start index must be lower than array length - 1");
        }

        int newLength = this.length - deleteCount + items.length;
        ArrayList<T> removed = new ArrayList<>(deleteCount);
        ArrayList<T> out = new ArrayList<>(newLength);

        int removeCount = deleteCount;
        boolean isFilled = false;

        for (int i = 0; i < this.elements.length; i++) {
            if (i >= start && i < start + deleteCount) {
                removed.add(this.elements[i]);
                removeCount--;
                continue;
            }

            if (removeCount == 0 && !isFilled) {
                for (T item : items) {
                    out.add(item);
                }
                isFilled = !isFilled;
            }

            out.add(this.elements[i]);
        }

        this.elements = (T[]) out.toArray();
        this.length = out.size();

        return new JSArray<>(removed.toArray());
    }

    public JSArray unshift(T t) throws SizeLimitExceededException {
        return new JSArray<>(new Object[] { t }).concat(this);
    }

    public Iterator<T> values() {
        return new JSArrayIterator<T>(i -> this.elements[i]);
    }

    public JSArray with(int index, T t) throws SizeLimitExceededException {
        if (index < 0 || index >= length) {
            throw new IllegalArgumentException("index value must be between 0 and array length - 1");
        }
        T[] out = Arrays.copyOf(this.elements, this.length);
        out[index] = t;

        return new JSArray<>(out);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("[");

        for (int i = 0; i < this.length; i++) {
            builder.append(this.elements[i]);
            if (i < this.length - 1) {
                builder.append(", ");
            }
        }

        builder.append("]");

        return builder.toString();
    }

}