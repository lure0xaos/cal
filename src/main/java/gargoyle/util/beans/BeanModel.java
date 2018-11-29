package gargoyle.util.beans;

import gargoyle.util.convert.DefaultConverters;
import gargoyle.util.load.MapLoader;
import gargoyle.util.reflect.ReflectionException;
import gargoyle.util.reflect.Reflections;
import gargoyle.util.resources.Resource;
import org.jetbrains.annotations.NotNull;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class BeanModel<B> {
    private static final String PROP_CLASS = "class";
    private final BeanInfo beanInfo;
    private final Class<B> type;

    public static <B> BeanModel<B> ofType(@NotNull Class<B> type) {
        return new BeanModel<>(type);
    }

    @NotNull
    public static <B> B load(Class<B> type, @NotNull Resource resource) {
        B bean = Reflections.newInstance(type);
        BeanModel<B> beanModel = ofType(type);
        beanModel.fill(bean, MapLoader.loadMap(resource));
        return bean;
    }

    @NotNull
    public static <B> B load(Class<B> type, @NotNull Map<String, String> map) {
        B bean = Reflections.newInstance(type);
        BeanModel<B> beanModel = ofType(type);
        beanModel.fill(bean, map);
        return bean;
    }

    @SuppressWarnings("BoundedWildcard")
    private BeanModel(@NotNull Class<B> type) {
        this.type = type;
        try {
            beanInfo = Introspector.getBeanInfo(type);
        } catch (IntrospectionException e) {
            String beanClassName = type.getName();
            throw new ReflectionException(MessageFormat.format("introspection failed of class {0}", beanClassName), e);
        }
    }

    @NotNull
    public B load(@NotNull Resource resource) {
        B bean = Reflections.newInstance(type);
        fill(bean, MapLoader.loadMap(resource));
        return bean;
    }

    @NotNull
    public B load(@NotNull Map<String, String> map) {
        B bean = Reflections.newInstance(type);
        fill(bean, map);
        return bean;
    }

    public void fill(@NotNull B bean, @NotNull Resource resource) {
        fill(bean, MapLoader.loadMap(resource));
    }

    @SuppressWarnings({"WeakerAccess", "NestedMethodCall"})
    public void fill(@NotNull B bean, @NotNull Map<String, String> map) {
        forEachProperty(property -> readWrite(map, bean, property));
    }

    private void readWrite(@NotNull Map<String, String> map, @NotNull B bean, PropertyDescriptor property) {
        Class<?> propertyType = property.getPropertyType();
        String propertyName = property.getName();
        String value = map.get(propertyName);
        Object converted = DefaultConverters.INSTANCE.convert(String.class, propertyType, value);
        writeProperty(bean, property, converted);
    }

    @SuppressWarnings("BoundedWildcard")
    private void forEachProperty(@NotNull Consumer<PropertyDescriptor> action) {
        @NotNull PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
        Arrays.stream(properties)
                .filter(property -> {
                    String propertyName = property.getName();
                    return !Objects.equals(PROP_CLASS, propertyName);
                })
                .forEach(action);
    }

    @NotNull
    public Optional<PropertyDescriptor> findProperty(@NotNull String name) {
        return findProperty(property -> {
            String propertyName = property.getName();
            return Objects.equals(propertyName, name);
        });
    }

    @SuppressWarnings({"BoundedWildcard", "WeakerAccess"})
    @NotNull
    public Optional<PropertyDescriptor> findProperty(@NotNull Predicate<PropertyDescriptor> predicate) {
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        return Arrays.stream(propertyDescriptors).filter(predicate).findFirst();
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public <T> T readProperty(@NotNull B bean, @NotNull PropertyDescriptor property) {
        String propertyName = property.getName();
        Method method = property.getReadMethod();
        if (method == null) {
            throw new ReflectionException(MessageFormat.format("no read property {0}", propertyName));
        }
        try {
            return (T) method.invoke(bean);
        } catch (@NotNull IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException(MessageFormat.format("cannot read property {0}", propertyName), e);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public <T> void writeProperty(@NotNull B bean, @NotNull PropertyDescriptor property, T value) {
        Method method = property.getWriteMethod();
        if (method != null) {
            try {
                method.invoke(bean, value);
            } catch (@NotNull IllegalAccessException | InvocationTargetException e) {
                String propertyName = property.getName();
                throw new ReflectionException(MessageFormat.format("cannot write property {0}", propertyName), e);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("BeanModel{type=%s}", type);
    }
}
