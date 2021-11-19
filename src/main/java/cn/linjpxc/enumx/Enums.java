package cn.linjpxc.enumx;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 提供常用的枚举工具。
 *
 * @author linjpxc
 */
public final class Enums {
    private Enums() {
    }

    private static final String TRUE = "true";
    private static final String FALSE = "false";

//    private static <T extends Valuable<?>> Class<?> getValueType(Class<T> type) {
//        if (!Valuable.class.isAssignableFrom(type)) {
//            throw new IllegalArgumentException("type not is Valuable.");
//        }
//        final Type valueType = getValueTypeArgument(getParentType(type));
//        if (!(valueType instanceof Class<?>)) {
//            throw new IllegalStateException();
//        }
//        return (Class<?>) valueType;
//    }
//
//    private static Type getParentType(Class<?> type) {
//        final Type[] genericInterfaces = type.getGenericInterfaces();
//        for (Type item : genericInterfaces) {
//            Class<?> midClass;
//            if (item instanceof ParameterizedType) {
//                final Type rawType = ((ParameterizedType) item).getRawType();
//                if (!(rawType instanceof Class<?>)) {
//                    throw new IllegalStateException("");
//                }
//                midClass = (Class<?>) rawType;
//            } else if (item instanceof Class<?>) {
//                midClass = (Class<?>) item;
//            } else {
//                throw new IllegalStateException();
//            }
//
//            if (Valuable.class.isAssignableFrom(midClass)) {
//                return item;
//            }
//        }
//
//        return type.getGenericSuperclass();
//    }

//    private static Type getValueTypeArgument(Type type) {
//        if (type instanceof Class<?>) {
//            return getValueTypeArgument((Class<?>) type);
//        }
//        if (type instanceof ParameterizedType) {
//            return getValueTypeArgument((ParameterizedType) type);
//        }
//        return null;
//    }
//
//    private static Type getValueTypeArgument(ParameterizedType parameterizedType) {
//        final Type rawType = parameterizedType.getRawType();
//        if (!(rawType instanceof Class<?>)) {
//            throw new IllegalStateException("");
//        }
//        if (rawType == Valuable.class) {
//            return parameterizedType.getActualTypeArguments()[0];
//        }
//        if (!Valuable.class.isAssignableFrom((Class<?>) rawType)) {
//            return null;
//        }
//
//        final Type valueTypeArgument = getValueTypeArgument(rawType);
//        if (valueTypeArgument instanceof ParameterizedType) {
//            return parameterizedType.getActualTypeArguments()[0];
//        }
//        if (valueTypeArgument instanceof TypeVariable) {
//            return getValueTypeArgument(parameterizedType, (TypeVariable<?>) valueTypeArgument);
//        }
//        return null;
//    }
//
//    private static Type getValueTypeArgument(ParameterizedType parameterizedType, TypeVariable<?> typeVariable) {
//        final Class<?> rawType = (Class<?>) parameterizedType.getRawType();
//        final TypeVariable<? extends Class<?>>[] typeParameters = rawType.getTypeParameters();
//
//        for (int i = 0; i < typeParameters.length; i++) {
//            if (typeParameters[i] == typeVariable) {
//                return parameterizedType.getActualTypeArguments()[i];
//            }
//        }
//        return null;
//    }
//
//    private static Type getValueTypeArgument(Class<?> type) {
//        if (!Valuable.class.isAssignableFrom(type)) {
//            return null;
//        }
//
//        return getValueTypeArgument(getParentType(type));
//    }

//    private static Class<?> getRawType(ParameterizedType parameterizedType) {
//        final Type rawType = parameterizedType.getRawType();
//        if (rawType instanceof Class<?>) {
//            return (Class<?>) rawType;
//        }
//        throw new IllegalStateException("");
//    }

    /**
     * 返回指定的枚举常量。可以是枚举值，也可以是枚举名称(忽略大小写敏感)的枚举常量。不自动转换基础类型。
     *
     * @param enumType 枚举类型class
     * @param value    自定的值
     * @param <E>      枚举类型
     * @param <V>      值类型
     * @return 返回枚举常量
     */
    public static <E extends Enum<E> & EnumValue<E, V>, V> E valueOf(Class<E> enumType, Object value) {
        return valueOf(enumType, value, false);
    }

    public static <E extends Enum<E> & EnumValue<E, V>, V> E valueOf(Class<E> enumType, Class<V> valueType, Object value) {
        return valueOf(enumType, valueType, value, false);
    }

    public static <E extends Enum<E> & EnumValue<E, V>, V> E valueOf(Class<E> enumType, Object value, boolean primitiveConvert) {
        return valueOf(enumType, getValueType(enumType), value, primitiveConvert);
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
    public static <E extends Enum<E> & EnumValue<E, V>, V> E valueOf(Class<E> enumType, Class<V> valueType, Object value, boolean primitiveConvert) {
        final E[] enumConstants = enumType.getEnumConstants();
        if (isPrimitiveOrEnumValueType(valueType, primitiveConvert, value)) {
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
        final Map<String, E> map = new HashMap<>(enumConstants.length);
        for (E item : enumConstants) {
            map.put(item.name(), item);
        }
        return map;
    }

    public static <E extends Enum<E> & EnumValue<E, V>, V> Map<V, E> valueMap(Class<E> enumType) {
        final E[] enumConstants = enumType.getEnumConstants();
        final Map<V, E> map = new HashMap<>(enumConstants.length);
        for (final E enumConstant : enumConstants) {
            map.put(enumConstant.value(), enumConstant);
        }
        return map;
    }

    private static boolean isPrimitiveOrEnumValueType(Class<?> valueType, boolean primitiveConvert, Object value) {
        return (isPrimitiveWrapper(valueType) && primitiveConvert)
                || valueType == value.getClass()
                || valueType.isAssignableFrom(value.getClass());
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
        if (TRUE.equalsIgnoreCase(value)) {
            return true;
        }
        if (FALSE.equalsIgnoreCase(value)) {
            return false;
        }
        return null;
    }

    private static boolean isEmpty(String value) {
        return value == null || value.trim().length() < 1;
    }
}
