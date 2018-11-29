package gargoyle.util.resources;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public interface Resource {
    boolean exists();

    @NotNull
    InputStream getInputStream();

    @NotNull
    OutputStream getOutputStream();

    @NotNull
    URL getUrl();
}
