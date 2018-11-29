package gargoyle.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Map;

@SuppressWarnings("HardCodedStringLiteral")
public final class Interpolation {
    private Interpolation() {
    }

    @NotNull
    @Contract("null, _ -> !null")
    public static String interpolate(@Nullable String text, @NotNull Map<String, Object> params) {
        @NotNull String ret = text != null ? text : "";
        if (text != null && !text.isBlank()) {
            for (@NotNull Map.Entry<String, Object> entry : params.entrySet()) {
                ret = ret.replace(MessageFormat.format("'{'{0}'}'", entry.getKey()), String.valueOf(entry.getValue()));
            }
        }
        return ret;
    }
}
