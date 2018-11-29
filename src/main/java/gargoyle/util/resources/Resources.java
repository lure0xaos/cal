package gargoyle.util.resources;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;

@SuppressWarnings("HardCodedStringLiteral")
public final class Resources {

    public static final String PROTOCOL_FILE = "file";

    private Resources() {
    }

    @NotNull
    @Contract("_ -> new")
    public static Resource getResource(@NotNull Path path) {
        return new FileSystemResource(path);
    }

    @NotNull
    @Contract("_, _ -> new")
    public static Resource getResource(@NotNull String location, @NotNull Class<?> loader) {
        ClassLoader classLoader = loader.getClassLoader();
        return new ClasspathResource(location, classLoader);
    }

    @NotNull
    @Contract("_, _ -> new")
    public static Resource getResource(@NotNull String location, @NotNull ClassLoader loader) {
        return new ClasspathResource(location);
    }

    @NotNull
    @Contract("_ -> new")
    public static Resource getResource(@NotNull URL url) {
        return new UrlResource(url);
    }

    @NotNull
    public static OutputStream getURLStream(@NotNull URL url) throws IOException {
        URLConnection connection = url.openConnection();
        return connection.getOutputStream();
    }

    @NotNull
    public static OutputStream getFileStream(@NotNull URL url) throws IOException {
        String location = url.toExternalForm();
        return Files.newOutputStream(Paths.get(location.substring(PROTOCOL_FILE.length() + 2)), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    public static URL getUrl(@NotNull String location, @NotNull ClassLoader loader) {
        URL url = loader.getResource(location);
        if (url == null) throw new RuntimeIOException(MessageFormat.format("no resource {0}", location));
        return url;
    }
}
