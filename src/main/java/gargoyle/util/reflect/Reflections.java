package gargoyle.util.reflect;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

@SuppressWarnings("WeakerAccess")
public final class Reflections {

    private Reflections() {
    }

    @NotNull
    public static <B> B newInstance(@NotNull Class<B> configClass) {
        try {
            Constructor<B> constructor = configClass.getConstructor();
            return constructor.newInstance();
        } catch (@NotNull InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            String configClassName = configClass.getName();
            throw new ReflectionException(MessageFormat.format("cannot instantiate class {0}", configClassName), e);
        }
    }

}
