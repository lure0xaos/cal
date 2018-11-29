package gargoyle.util;

import gargoyle.util.asserts.Assertions;
import gargoyle.util.beans.BeanModel;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Args {
    private static final String EQ = "=";

    private Args() {
    }

    @NotNull
    public static <C> C parseArgs(@NotNull Class<C> configClass, @NotNull String[] args) {
        BeanModel<C> beanModel = BeanModel.ofType(configClass);
        return beanModel.load(parseArgs(args));
    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public static Map<String, String> parseArgs(@NotNull String[] args) {
        //noinspection NestedMethodCall
        return Arrays.stream(args).map(arg -> arg.split(EQ, 2))
                .filter((pair) -> pair.length > 1)
                .collect(Collectors.toMap(pair -> {
                            String key = pair[0];
                            return key.trim();
                        }, split -> {
                            String value = split[1];
                            return value.trim();
                        },
                        (key, val) -> val,
                        () -> new LinkedHashMap<>(args.length)));
    }

    @NotNull
    public static Map<String, String> parseArgs(@NotNull String[] keys, @NotNull String[] args) {
        Assertions.assertTrue(keys.length >= args.length, "no matched args name");
        //noinspection ChainedMethodCall
        return IntStream.range(0, args.length).boxed().
                collect(Collectors.toMap(
                        i -> keys[i],
                        i -> args[i],
                        (key, val) -> val,
                        () -> new LinkedHashMap<>(keys.length)));
    }
}
