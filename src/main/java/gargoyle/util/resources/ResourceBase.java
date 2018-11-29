package gargoyle.util.resources;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;

abstract class ResourceBase implements Resource {
    @NotNull
    private final URL url;

    protected ResourceBase(@NotNull URL url) {
        this.url = url;
    }

    @SuppressWarnings("MethodWithMultipleReturnPoints")
    @Override
    public final boolean exists() {
        try (InputStream ignored = url.openStream()) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @NotNull
    @Override
    public final InputStream getInputStream() {
        try {
            URLConnection connection = url.openConnection();
            return connection.getInputStream();
        } catch (IOException e) {
            throw new RuntimeIOException(MessageFormat.format("cannot open stream to read from {0}", url), e);
        }
    }

    @SuppressWarnings({"MethodWithMultipleReturnPoints", "SwitchStatementWithTooFewBranches", "LocalVariableHidesMemberVariable"})
    @NotNull
    @Override
    public final OutputStream getOutputStream() {
        URL url = this.url;
        try {
            String protocol = url.getProtocol();
            switch (protocol) {
                case Resources.PROTOCOL_FILE:
                    return Resources.getFileStream(url);
                default:
                    return Resources.getURLStream(url);
            }
        } catch (IOException e) {
            throw new RuntimeIOException(MessageFormat.format("cannot open to write {0}", url), e);
        }
    }

    @NotNull
    @Override
    public final URL getUrl() {
        return url;
    }

    @Override
    public final String toString() {
        Class<? extends ResourceBase> resourceClass = getClass();
        String className = resourceClass.getName();
        return String.format("%s{url=%s}", className, url);
    }
}
