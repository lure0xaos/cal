package gargoyle.calendar;

import gargoyle.calendar.core.CalConfig;
import gargoyle.calendar.core.CalCore;
import gargoyle.calendar.core.CalUtil;
import gargoyle.util.Args;
import gargoyle.util.beans.BeanModel;
import gargoyle.util.resources.FileSystemResource;
import gargoyle.util.resources.Resource;
import gargoyle.util.resources.Resources;
import org.jetbrains.annotations.NotNull;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Year;
import java.util.Locale;
import java.util.Map;

public final class Cal {
    public static final String CONFIG_LOCATION = "cal.properties";
    private static final String CMD_OUT = "year";
    private static final String CMD_SHOW = "show";
    private static final String CMD_YEAR = "year";
    private static final String SUFFIX = ".png";
    private static final String FORMAT = "PNG";

    private Cal() {
    }

    public static void main(@NotNull String[] args) throws IOException, URISyntaxException {
        Locale locale = Locale.getDefault();
        @NotNull Map<String, String> cmd = Args.parseArgs(new String[]{CMD_YEAR, CMD_OUT, CMD_SHOW}, args);
        @NotNull Year year = Year.of(Integer.parseInt(cmd.getOrDefault(CMD_YEAR, String.valueOf(CalUtil.getCurrentYear()))));
        @NotNull Resource configResource = Resources.getResource(CONFIG_LOCATION, CalCore.class);
        @NotNull CalConfig configuration = BeanModel.load(CalConfig.class, configResource);
        @NotNull Resource out = new FileSystemResource(Paths.get(cmd.getOrDefault(CMD_OUT, year + SUFFIX)));
        new CalCore(configuration).createWrite(out, year, locale, FORMAT);
        if (!GraphicsEnvironment.isHeadless() && Desktop.isDesktopSupported()) {
            if (Boolean.parseBoolean(cmd.getOrDefault(CMD_SHOW, Boolean.FALSE.toString()))) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    URL url = out.getUrl();
                    URI uri = url.toURI();
                    desktop.browse(uri);
                }
            }
        }
    }
}
