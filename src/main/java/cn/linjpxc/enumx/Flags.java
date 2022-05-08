package cn.linjpxc.enumx;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提供 {@link FlagValue} 的常用工具。
 *
 * @author linjpxc
 */
public final class Flags {

    private Flags() {
    }

    private static final ConcurrentHashMap<Class<?>, List<FlagWrapper<?, ?>>> FLAG_WRAPPERS = new ConcurrentHashMap<>();

    public static <F extends FlagValue<F, V>, V> Class<V> getValueType(Class<F> clazz) {
        return Values.getValueType(clazz);
    }

    public static <F extends FlagValue<F, V>, V> String toString(F flag) {
        return toString(flag, " | ");
    }

    static <F extends FlagValue<F, V>, V> String toString(F flag, String delimiter) {
        final F[] values = getValues(flag.getDeclaringClass());
        for (F value : values) {
            if (value.equals(flag)) {
                return flag.name();
            }
        }

        final StringBuilder builder = new StringBuilder();
        F f = null;
        for (F value : values) {
            if (flag.hasFlag(value)) {
                if (builder.length() > 0) {
                    builder.append(delimiter);
                }
                builder.append(value.name());

                if (f == null) {
                    f = value;
                } else {
                    f = f.addFlag(value);
                }

                if (f.compareTo(flag) >= 0) {
                    break;
                }
            }
        }

        if (f != null && !f.equals(flag)) {
            return flag.name();
        }

        if (builder.length() > 0) {
            return builder.toString();
        }
        return flag.name();
    }

    @SuppressWarnings({"unchecked"})
    public static <F extends FlagValue<F, V>, V> F[] getValues(Class<F> clazz) {
        return getFlagWrappers(clazz)
                .stream()
                .map(FlagWrapper::getValue)
                .toArray(length -> (F[]) Array.newInstance(clazz, length));
    }

    @SuppressWarnings({"unchecked"})
    public static <F extends FlagValue<F, V>, V> F[] getDefineValues(Class<F> clazz) {
        return getFlagWrappers(clazz)
                .stream()
                .filter(item -> item.getFlag().isDefined())
                .map(FlagWrapper::getValue)
                .toArray(length -> (F[]) Array.newInstance(clazz, length));
    }

    @SuppressWarnings({"unchecked"})
    static <F extends FlagValue<F, V>, V> List<FlagWrapper<F, V>> getFlagWrappers(Class<F> clazz) {
        if (!FlagValue.class.isAssignableFrom(clazz)) {
            throw new RuntimeException("Not is FlagValue class.");
        }
        return (List<FlagWrapper<F, V>>) (Object) FLAG_WRAPPERS.computeIfAbsent(clazz, key -> {
            try {
                final Field[] fields = clazz.getDeclaredFields();
                final List<FlagWrapper<?, ?>> list = new ArrayList<>();
                for (Field field : fields) {
                    final Flag flag = field.getDeclaredAnnotation(Flag.class);
                    if (flag == null) {
                        continue;
                    }

                    if (!Modifier.isStatic(field.getModifiers())) {
                        System.err.printf("%s the field is not static: %s%n", clazz, field.getName());
                        continue;
                    }

                    field.setAccessible(true);

                    final F value = (F) field.get(null);
                    if (value instanceof TextFlag<?>) {
                        final Field valueField = AbstractFlag.class.getDeclaredField("value");
                        valueField.setAccessible(true);
                        final String tmp = (String) valueField.get(value);
                        if (tmp.length() < 1) {
                            final Field modifiersField = Field.class.getDeclaredField("modifiers");
                            modifiersField.setAccessible(true);
                            modifiersField.setInt(valueField, valueField.getModifiers() & ~Modifier.FINAL);

                            valueField.set(value, field.getName().toUpperCase(Locale.ROOT));
                        }
                    }
                    list.add(new FlagWrapper<>(flag, field.getName(), value));
                }
                return list;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
