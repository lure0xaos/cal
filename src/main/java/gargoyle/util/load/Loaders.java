package gargoyle.util.load;

import gargoyle.util.resources.Resource;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"WeakerAccess", "rawtypes"})
public class Loaders {
    @NotNull
    private final Map<Class, Loader> loaders;

    protected Loaders() {
        loaders = new LinkedHashMap<>();
    }

    public Loaders(@NotNull Map<Class, Loader> loaders) {
        this.loaders = new LinkedHashMap<>(loaders);
    }

    protected final <T> Loaders addLoader(@NotNull Class<T> targetType, @NotNull Loader<T> loader) {
        if (canLoad(targetType)) {
            throw new IllegalArgumentException(MessageFormat.format("loader {0} already exists", targetType));
        }
        loaders.put(targetType, loader);
        return this;
    }

    @Contract(pure = true)
    public final <T> boolean canLoad(@NotNull Class<T> targetType) {
        return loaders.containsKey(targetType);
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public final <T> T load(@NotNull Class<T> targetType, @NotNull Resource value) throws IOException {
        if (!loaders.containsKey(targetType)) throw new UnsupportedOperationException(MessageFormat.format(
                "loader {0} is not supported yet", targetType));
        Loader<T> loader = loaders.get(targetType);
        return loader.load(value);
    }

    @Override
    public final String toString() {
        return String.format("Loaders{loaders=%s}", loaders);
    }
}
