package gargoyle.util.config;

import gargoyle.util.convert.DefaultConverters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;


public final class Configuration {
    private final Map<String, String> map;

    Configuration(Map<String, String> map) {
        this.map = Collections.unmodifiableMap(map);
    }

    public String get(String key) {
        return map.get(key);
    }

    @Override
    public String toString() {
        return String.format("Configuration{linkedHashMap=%s}", map);
    }

    @Nullable
    public <T> T getAs(@NotNull String key, @NotNull Class<T> type) {
        String value = get(key);
        return DefaultConverters.INSTANCE.convert(type, value);
    }

    public String getOrDefault(String key, String defaultValue) {
        String v = get(key);
        return v != null ? v : defaultValue;
    }
}
