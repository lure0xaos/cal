package gargoyle.util.resources;

import org.jetbrains.annotations.NotNull;

import java.net.URL;

public class ClasspathResource extends ResourceBase {
    public ClasspathResource(@NotNull String location) {
        this(location, ClasspathResource.class.getClassLoader());
    }

    public ClasspathResource(@NotNull String location, @NotNull ClassLoader classLoader) {
        super(Resources.getUrl(location, classLoader));
    }

    public ClasspathResource(@NotNull URL location) {
        super(location);
    }


}
