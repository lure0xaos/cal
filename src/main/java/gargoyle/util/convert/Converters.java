package gargoyle.util.convert;

import gargoyle.util.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;


@SuppressWarnings({"WeakerAccess", "rawtypes"})
public class Converters {
    @NotNull
    private final Map<Tuple<Class, Class>, Function> converters;

    protected Converters() {
        converters = new LinkedHashMap<>();
    }

    public Converters(@NotNull Map<Tuple<Class, Class>, Function> converters) {
        this.converters = new LinkedHashMap<>(converters);
    }

    protected final <S, T> Converters declareConverter(@NotNull Class<S> sourceType, @NotNull Class<T> targetType, @NotNull Function<S, T> converter) {
        if (canConvert(sourceType, targetType)) {
            throw new IllegalArgumentException(MessageFormat.format("converter {0}->{1} already exists",
                    sourceType, targetType));
        }
        converters.put(new Tuple<>(sourceType, targetType), converter);
        return this;
    }

    public final <S, T> boolean canConvert(@NotNull Class<S> sourceType, @NotNull Class<T> targetType) {
        return converters.containsKey(new Tuple<Class, Class>(sourceType, targetType));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public final <S, T, V extends S> T convert(@NotNull Class<T> targetType, @Nullable V value) {
        if (value == null) return null;
        else {
            Class<?> valueClass = value.getClass();
            return convert((Class<S>) valueClass, targetType, value);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public final <S, T, V extends S> T convert(@NotNull Class<S> sourceType, @NotNull Class<T> targetType, @Nullable V value) {
        return value == null ? null : (sourceType == targetType ? (T) value :
                (T) Optional.ofNullable(converters.get(new Tuple<Class, Class>(sourceType, targetType)))
                        .orElseThrow(() -> new UnsupportedOperationException(MessageFormat.format(
                                "converter {0}->{1} is not supported yet", sourceType, targetType)))
                        .apply(value));
    }
}
