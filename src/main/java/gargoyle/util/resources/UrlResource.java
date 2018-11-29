package gargoyle.util.resources;

import org.jetbrains.annotations.NotNull;

import java.net.URL;

public class UrlResource extends ResourceBase {
    public UrlResource(@NotNull URL url) {
        super(url);
    }
}
