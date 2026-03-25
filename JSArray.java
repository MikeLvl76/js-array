import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import javax.naming.SizeLimitExceededException;

public class JSArray<T> implements JSArrayUtils {

    private T[] elements;
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

    @SuppressWarnings("unchecked")
    public JSArray() {
        this.elements = (T[]) new Object[0];
        this.length = 0;
    }

    @SuppressWarnings("unchecked")
    public JSArray(int _length) throws SizeLimitExceededException {
        if (length > MAX_CAPACITY) {
            throw new SizeLimitExceededException("cannot exceed max size: " + MAX_CAPACITY);
        }
        if (_length < 0) {
            throw new IllegalArgumentException("negative length not allowed");
        }
        this.length = _length;
        this.elements = (T[]) new Object[_length];
    }

    public JSArray(@SuppressWarnings("unchecked") T... values) throws SizeLimitExceededException {
        if (values.length > MAX_CAPACITY) {
            throw new SizeLimitExceededException("cannot exceed max size: " + MAX_CAPACITY);
        }
        this.elements = values;
        this.length = values.length;
    }

    public static JSArray<String> from(String text) throws SizeLimitExceededException {
        return new JSArray<>(text.split(""));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Object> JSArray<T> from(JSArray<?> array, Function<T, ?> mapper)
            throws SizeLimitExceededException {

        T[] out = JSArrayUtils.copyArray((T[]) array.toPrimitiveArray());

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

    public JSArray<T> concat(JSArray<T> array) throws SizeLimitExceededException {
        T[] result = concatArrays(this.elements, array.toPrimitiveArray());
        return new JSArray<>(result);
    }

    public JSArray<T> copyWithin(int target, int start, int end) throws SizeLimitExceededException {
        if (target > end) {
            throw new IllegalArgumentException("target index must be lower than end index");
        }

        if (start > end || start == end) {
            throw new IllegalArgumentException("start index must be lower than end index");
        }

        T[] out = JSArrayUtils.copyArray(this.elements);
        JSArray<T> range = this.slice(start, end);

        int i = target;
        for (int j = 0; j < range.size(); j++) {
            out[i] = range.at(j);
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

    public JSArray<T> fill(T value, int start, int end) throws SizeLimitExceededException {
        if (start >= end) {
            throw new IllegalArgumentException("start index must be lower than end index");
        }

        if (start >= this.length || end > this.length) {
            throw new IllegalArgumentException("indices lust be lower than array size");
        }

        T[] out = JSArrayUtils.copyArray(this.elements);

        for (int i = start; i < end; i++) {
            out[i] = value;
        }

        return new JSArray<>(out);
    }

    @SuppressWarnings("unchecked")
    public JSArray<T> filter(BiPredicate<T, Integer> predicate) throws SizeLimitExceededException {
        T[] filtered = (T[]) new Object[this.length];
        int i = 0;

        for (int j = 0; j < this.length; j++) {
            if (predicate.test(this.elements[j], j)) {
                filtered[i] = this.elements[j];
                i++;
            }
        }

        T[] result = removeNullValues(filtered);

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

    @SuppressWarnings("unchecked")
    private T[] deepFlat(T[] array, int depth) {
        int dimension = getDimension(array);

        if (dimension <= 1 || depth <= 0) {
            return array;
        }

        T[] out = (T[]) new Object[0];

        for (int i = 1; i < array.length; i++) {
            T[] concatenated = concatArrays(deepFlat((T[]) array[i - 1], depth - 1),
                    deepFlat((T[]) array[i], depth - 1));

            out = concatArrays(out, concatenated);
        }

        return this.deepFlat(out, depth - 1);
    }

    public JSArray<T> flat(int depth) throws SizeLimitExceededException {
        T[] out = this.deepFlat(this.elements, depth);
        return new JSArray<>(out);
    }

    public <U extends Object> JSArray<U> flatMap(BiFunction<T, Integer, U> mapper, int depth)
            throws SizeLimitExceededException {
        return new JSArray<>(this.elements).flat(depth).map(mapper);
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
    public <U extends Object> JSArray<U> map(BiFunction<T, Integer, U> mapper) throws SizeLimitExceededException {
        U[] out = (U[]) new Object[this.length];

        for (int i = 0; i < this.length; i++) {
            out[i] = mapper.apply(this.elements[i], i);
        }

        return new JSArray<>(out);
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        T popped = null;

        if (this.length == 0) {
            return popped;
        }

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

        this.elements = concatArrays(this.elements, items);
        this.length = this.elements.length;
        return this.length;
    }

    @SuppressWarnings("unchecked")
    public <U extends Object> JSArray<T> reduce(TriFunction<U[], T, Integer, U[]> reducer, U[] initialValue)
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
    public <U extends Object> JSArray<T> reduceRight(TriFunction<U[], T, Integer, U[]> reducer, U[] initialValue)
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

    @SuppressWarnings("unchecked")
    public JSArray<T> reverse() throws SizeLimitExceededException {
        T[] reversed = (T[]) new Object[this.length];

        for (int i = 0; i < this.length; i++) {
            reversed[i] = this.elements[this.length - 1 - i];
        }

        return new JSArray<>(reversed);
    }

    @SuppressWarnings("unchecked")
    public T shift() {
        T shifted = null;

        if (this.length == 0) {
            return shifted;
        }

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

    @SuppressWarnings("unchecked")
    public JSArray<T> slice(int start, int end) throws SizeLimitExceededException {
        if (start >= end) {
            throw new IllegalArgumentException("start index must be lower than end index");
        }

        T[] out = (T[]) new Object[end - start];

        int i = 0;
        for (int j = start; j < end; j++) {
            out[i] = this.elements[j];
            i++;
        }

        return new JSArray<>(out);
    }

    public boolean some(BiPredicate<T, Integer> predicate) {
        for (int i = 0; i < this.length; i++) {
            if (predicate.test(this.elements[i], i)) {
                return true;
            }
        }

        return false;
    }

    public JSArray<T> sort(BiFunction<T, T, Integer> comparator) throws SizeLimitExceededException {
        T[] out = JSArrayUtils.copyArray(this.elements);

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
    public JSArray<T> splice(int start, int deleteCount, T... items) throws SizeLimitExceededException {
        int newLength = this.length - deleteCount + items.length;

        if (newLength > MAX_CAPACITY) {
            throw new SizeLimitExceededException("too much values to insert");
        }

        if (this.length < deleteCount || start + deleteCount > this.length) {
            throw new IllegalArgumentException("cannot delete more values than array can contain");
        }

        if (start >= this.length) {
            throw new IllegalArgumentException("start index must be lower than array length - 1");
        }

        JSArray<T> removed = deleteCount == 0 ? new JSArray<>() : this.slice(start, start + deleteCount);
        JSArray<T> left = this.slice(0, start);
        JSArray<T> right = this.slice(start + deleteCount, length);

        T[] out = concatArrays(concatArrays(left.toPrimitiveArray(), items), right.toPrimitiveArray());

        this.elements = out;
        this.length = out.length;

        return removed;
    }

    @SuppressWarnings("unchecked")
    public JSArray<T> unshift(T t) throws SizeLimitExceededException {
        return new JSArray<>((T[]) new Object[] { t }).concat(this);
    }

    public Iterator<T> values() {
        return new JSArrayIterator<T>(i -> this.elements[i]);
    }

    public JSArray<T> with(int index, T t) throws SizeLimitExceededException {
        if (index < 0 || index >= length) {
            throw new IllegalArgumentException("index value must be between 0 and array length - 1");
        }

        T[] out = JSArrayUtils.copyArray(this.elements);
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