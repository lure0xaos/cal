package gargoyle.util.load;

import gargoyle.util.resources.Resource;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@FunctionalInterface
public interface Loader<T> {
    @NotNull
    T load(@NotNull Resource resource) throws IOException;
}
