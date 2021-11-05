package cn.linjpxc.enumx;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 提供枚举相关的工具方法
 */
public final class Enums {
    private Enums() {
    }

    public static <E extends Enum<E> & EnumValue<E, V>, V> E valueOf(Class<E> enumType, Object value) {
        return valueOf(enumType, value, false);
    }

    /**
     * 返回指定的枚举值，也可以是枚举名称(不区分大小写)的枚举常量。
     *
     * @param enumType         枚举类型class
     * @param value            自定的值
     * @param <E>              枚举类型
     * @param <V>              值类型
     * @param primitiveConvert 基础类型是否自动转换
     * @return 返回枚举常量
     */
    public static <E extends Enum<E> & EnumValue<E, V>, V> E valueOf(Class<E> enumType, Object value, boolean primitiveConvert) {
        final Class<V> valueType = getValueType(enumType);
        final E[] enumConstants = enumType.getEnumConstants();
        if ((isPrimitiveWrapper(valueType) && primitiveConvert)
                || valueType == value.getClass()
                || valueType.isAssignableFrom(value.getClass())) {
            final Object primitiveValue = convertPrimitive(valueType, value, primitiveConvert);
            for (E item : enumConstants) {
                if (item.value().equals(value)) {
                    return item;
                } else if (primitiveValue != null && primitiveValue.equals(item.value())) {
                    return item;
                }
            }
        }

        final String name = value.toString();
        for (E item : enumConstants) {
            if (item.name().equals(name)) {
                return item;
            }
            if (item.name().equalsIgnoreCase(name)) {
                return item;
            }
        }
        throw new IllegalArgumentException("No enum constant " + enumType.getCanonicalName() + "." + name);
    }

    /**
     * 返回指定名称的枚举常量。该名称与此类型中声明的枚举常量的标识，忽略大小写比较（不允许使用多余的空白字符）。
     *
     * @param enumType 枚举类型的 class
     * @param name     枚举常量的名称
     * @param <E>      枚举类型
     * @return 具有指定名称的枚举常量
     */
    public static <E extends Enum<E>> E valueOfIgnoreCase(Class<E> enumType, String name) {
        if (!enumType.isEnum()) {
            throw new IllegalArgumentException("class not enum.");
        }
        final E[] values = enumType.getEnumConstants();
        for (E item : values) {
            if (item.name().equalsIgnoreCase(name)) {
                return item;
            }
        }
        throw new IllegalArgumentException("No enum constant " + enumType.getCanonicalName() + "." + name);
    }

    /**
     * 判断指定枚举类型的枚举常量是否存在。
     *
     * @param enumType 枚举类型 class。
     * @param name     枚举常量名称。
     * @param <E>      枚举类型
     * @return 若常量存在，则返回true，否则返回false。
     */
    public static <E extends Enum<E>> boolean exists(Class<E> enumType, String name) {
        if (isEmpty(name)) {
            return false;
        }
        final E[] enumConstants = enumType.getEnumConstants();
        for (E item : enumConstants) {
            if (item.name().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断指定枚举类型的枚举常量是否存在。忽略大小写。
     *
     * @param enumType 枚举类型 class。
     * @param name     枚举常量名称。
     * @param <E>      枚举类型
     * @return 若常量存在，则返回true，否则返回false。
     */
    public static <E extends Enum<E>> boolean existsIgnoreCase(Class<E> enumType, String name) {
        if (isEmpty(name)) {
            return false;
        }
        final E[] enumConstants = enumType.getEnumConstants();
        for (E item : enumConstants) {
            if (item.name().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断指定枚举类型的枚举值是否存在。
     *
     * @param enumType 枚举类型 class。
     * @param value    枚举值
     * @param <E>      枚举类型
     * @param <V>      枚举值类型
     * @return 若枚举值存在，则返回true，否则返回false。
     */
    public static <E extends Enum<E> & EnumValue<E, V>, V> boolean exists(Class<E> enumType, V value) {
        final E[] enumConstants = enumType.getEnumConstants();
        for (E item : enumConstants) {
            if (item.value().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static <E extends Enum<E>> List<E> list(Class<E> enumType) {
        return new ArrayList<E>(Arrays.asList(enumType.getEnumConstants()));
    }

    public static <E extends Enum<E>> Map<String, E> map(Class<E> enumType) {
        final E[] enumConstants = enumType.getEnumConstants();
        final Map<String, E> map = new HashMap<>();
        for (E item : enumConstants) {
            map.put(item.name(), item);
        }
        return map;
    }

    public static <E extends Enum<E> & EnumValue<E, V>, V> Map<V, E> valueMap(Class<E> enumType) {
        final E[] enumConstants = enumType.getEnumConstants();
        final Map<V, E> map = new HashMap<>();
        for (E item : enumConstants) {
            map.put(item.value(), item);
        }
        return map;
    }

    private static <E extends Enum<E> & EnumValue<E, V>, V> boolean isValueType(Class<E> enumType, Class<?> type) {
        try {
            final Method method = enumType.getDeclaredMethod("value");
            final Class<?> returnType = method.getReturnType();
            return returnType == type && returnType.isAssignableFrom(type);
        } catch (Exception ignored) {
            return false;
        }
    }

    @SuppressWarnings({"unchecked"})
    private static <E extends Enum<E> & EnumValue<E, V>, V> Class<V> getValueType(Class<E> enumType) {
        try {
            final Method method = enumType.getDeclaredMethod("value");
            return (Class<V>) method.getReturnType();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isPrimitiveWrapper(Class<?> clazz) {
        return clazz == Boolean.class
                || clazz == Short.class
                || clazz == Integer.class
                || clazz == Long.class
                || clazz == Float.class
                || clazz == Double.class
                || clazz == Byte.class
                || clazz == Character.class;
    }

    private static Object convertPrimitive(Class<?> clazz, Object value, boolean primitiveConvert) {
        if (!primitiveConvert) {
            return null;
        }
        if (clazz == value.getClass()) {
            return value;
        }
        if (clazz == Boolean.class) {
            return toBoolean(value.toString());
        }
        if (clazz == Byte.class) {
            return toByte(value.toString());
        }
        if (clazz == Short.class) {
            return toShort(value.toString());
        }
        if (clazz == Integer.class) {
            return toInteger(value.toString());
        }
        if (clazz == Long.class) {
            return toLong(value.toString());
        }
        if (clazz == Float.class) {
            return toFloat(value.toString());
        }
        if (clazz == Double.class) {
            return toDouble(value.toString());
        }
        if (clazz == Character.class) {
            return toCharacter(value.toString());
        }

        return null;
    }

    private static Character toCharacter(String value) {
        if (value.length() == 1) {
            return value.toCharArray()[0];
        }
        return null;
    }

    private static Double toDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Float toFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Long toLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Integer toInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Short toShort(String value) {
        try {
            return Short.parseShort(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Byte toByte(String value) {
        try {
            return Byte.parseByte(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Boolean toBoolean(String value) {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        return null;
    }

    private static boolean isEmpty(String value) {
        return value == null || value.trim().length() < 1;
    }
}
