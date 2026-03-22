import static org.junit.jupiter.api.Assertions.*;

import javax.naming.SizeLimitExceededException;

import org.junit.jupiter.api.*;

public class JSArrayTest {

    @Test
    @DisplayName("should return empty JSArray")
    public void testEmptyArray() {
        final JSArray<?> array = new JSArray<>();
        assertAll("Grouped assertions of JSArray", () -> {
            assertArrayEquals(array.toPrimitiveArray(), new Object[0]);
            assertEquals(array.size(), 0);
        });
    }

    @Test
    @DisplayName("should return JSArray of 10 null values")
    public void testArray() throws SizeLimitExceededException {
        int length = 10;
        final JSArray<?> array = new JSArray<>(length);
        assertAll("Grouped assertions of JSArray", () -> {
            assertArrayEquals(array.toPrimitiveArray(), new Object[length]);
            assertEquals(array.size(), length);
        });
    }

}
