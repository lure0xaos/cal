package gargoyle.util.asserts;

import org.jetbrains.annotations.Contract;


@SuppressWarnings({"HardCodedStringLiteral", "StaticMethodOnlyUsedInOneClass"})
public final class Assertions {
    private Assertions() {
    }

    @SuppressWarnings("BooleanParameter")
    @Contract("false, _ -> fail")
    public static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionException(message);
    }

    @Contract("null, _ -> fail")
    public static void assertNotNull(Object value, String message) {
        if (value == null) throw new AssertionException(message);
    }
}
