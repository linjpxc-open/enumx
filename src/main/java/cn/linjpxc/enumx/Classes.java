package cn.linjpxc.enumx;

/**
 * @author linjpxc
 */
final class Classes {

    private Classes() {
    }

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    static boolean isPrimitiveWrapper(Class<?> clazz) {
        return clazz == Boolean.class
                || clazz == Short.class
                || clazz == Integer.class
                || clazz == Long.class
                || clazz == Float.class
                || clazz == Double.class
                || clazz == Byte.class
                || clazz == Character.class;
    }

    static Class<?> convertPrimitiveType(Class<?> clazz) {
        if (clazz == Boolean.class) {
            return boolean.class;
        }
        if (clazz == Short.class) {
            return short.class;
        }
        if (clazz == Integer.class) {
            return int.class;
        }
        if (clazz == Long.class) {
            return long.class;
        }
        if (clazz == Float.class) {
            return float.class;
        }
        if (clazz == Double.class) {
            return double.class;
        }
        if (clazz == Byte.class) {
            return byte.class;
        }
        if (clazz == Character.class) {
            return char.class;
        }
        return clazz;
    }

    static Object convertPrimitive(Class<?> clazz, Object value) {
        if (value == null) {
            return null;
        }
        if (clazz == value.getClass()) {
            return value;
        }

        final String str = value.toString();
        if (Strings.isEmpty(str)) {
            return null;
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return toBoolean(value);
        }
        if (clazz == Byte.class || clazz == byte.class) {
            return toByte(value);
        }
        if (clazz == Short.class || clazz == short.class) {
            return toShort(value);
        }
        if (clazz == Integer.class || clazz == int.class) {
            return toInteger(value);
        }
        if (clazz == Long.class || clazz == long.class) {
            return toLong(value);
        }
        if (clazz == Float.class || clazz == float.class) {
            return toFloat(value);
        }
        if (clazz == Double.class || clazz == double.class) {
            return toDouble(value);
        }
        if (clazz == Character.class || clazz == char.class) {
            return toCharacter(value);
        }
        throw new IllegalArgumentException("Class not primitive type.");
    }

    private static Object toBoolean(Object value) {
        final String str = value.toString();
        if (TRUE.equalsIgnoreCase(str)) {
            return true;
        }
        if (FALSE.equalsIgnoreCase(str)) {
            return false;
        }
        return Boolean.parseBoolean(str);
    }

    private static Object toByte(Object value) {
        return Byte.parseByte(value.toString());
    }

    private static Object toShort(Object value) {
        return Short.parseShort(value.toString());
    }

    private static Object toInteger(Object value) {
        return Integer.parseInt(value.toString());
    }

    private static Object toLong(Object value) {
        return Long.parseLong(value.toString());
    }

    private static Object toFloat(Object value) {
        return Float.parseFloat(value.toString());
    }

    private static Object toDouble(Object value) {
        return Double.parseDouble(value.toString());
    }

    private static Object toCharacter(Object value) {
        final String str = value.toString();
        if (str.length() == 1) {
            return str.toCharArray()[0];
        }
        return null;
    }
}
