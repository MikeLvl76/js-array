public interface JSArrayUtils {
    final int MAX_CAPACITY = Integer.MAX_VALUE - 1;

    default <T> int getDimension(T[] array) {
        int dimension = 0;

        Class<?> cls = array.getClass();

        while (cls.isArray()) {
            cls = cls.getComponentType();
            dimension++;
        }

        return dimension;
    }

    @SuppressWarnings("unchecked")
    default <T> T[] concatArrays(T[] a, T[] b) {
        int newLength = a.length + b.length;
        assert newLength <= MAX_CAPACITY : "cannot concatenata arrays: size exceeded";
        T[] c = (T[]) new Object[newLength];

        int i = 0;
        int j = 0;
        int k = 0;

        while(i < c.length) {
            if (j < a.length) {
                c[i] = a[j];
                j++;
            } else {
                c[i] = b[k];
                k++;
            }
            i++;   
        }

        return c;
    }
}
