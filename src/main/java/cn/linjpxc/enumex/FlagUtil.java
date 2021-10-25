package cn.linjpxc.enumex;

import sun.reflect.ConstructorAccessor;
import sun.reflect.FieldAccessor;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.*;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class FlagUtil {
    private FlagUtil() {
    }

    private static final ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();

    static Class<?> getFlagValueClass(Class<? extends Flag<?, ?, ?>> clazz, boolean isPrimitive) {
        if (IntFlag.class.isAssignableFrom(clazz)) {
            return isPrimitive ? int.class : Integer.class;
        }
        if (LongFlag.class.isAssignableFrom(clazz)) {
            return isPrimitive ? long.class : Long.class;
        }
        if (BigFlag.class.isAssignableFrom(clazz)) {
            return BigInteger.class;
        }

        throw new IllegalArgumentException();
    }

    @SuppressWarnings({"unchecked"})
    static <T extends java.lang.Enum<T>> T addFlag(Class<T> enumType, String enumName, Class<?>[] additionalTypes, Object[] additionalValues) throws Exception {
        Field valuesField = null;
        final Field[] fields = enumType.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().contains("$VALUES")) {
                valuesField = field;
                break;
            }
        }
        if (valuesField == null) {
            throw new IllegalArgumentException();
        }

        AccessibleObject.setAccessible(new Field[]{valuesField}, true);

        T[] previousValues = (T[]) valuesField.get(enumType);
        final List<T> values = new ArrayList<>(Arrays.asList(previousValues));

        T newValue = (T) makeEnum(enumType, enumName, values.size(), additionalTypes, additionalValues);
        values.add(newValue);

        setFailsafeFieldValue(valuesField, null, values.toArray((T[]) Array.newInstance(enumType, 0)));

        cleanEnumCache(enumType);

        return newValue;
    }

    private static void setFailsafeFieldValue(Field field, Object target, Object value) throws NoSuchFieldException, IllegalAccessException {
        setAccessible(field, true);

        int modifiers = field.getModifiers();
        modifiers &= ~Modifier.FINAL;

        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        setAccessible(modifiersField, true);
        modifiersField.setInt(field, modifiers);

        final FieldAccessor fieldAccessor = reflectionFactory.newFieldAccessor(field, false);
        fieldAccessor.set(target, value);
    }

    private static void blankField(Class<? extends java.lang.Enum<?>> enumClass, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        final Field[] declaredFields = Class.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getName().contains(fieldName)) {
                AccessibleObject.setAccessible(new Field[]{field}, true);
                setFailsafeFieldValue(field, enumClass, null);
                break;
            }
        }
    }

    private static void cleanEnumCache(Class<? extends java.lang.Enum<?>> enumClass) throws NoSuchFieldException, IllegalAccessException {
        blankField(enumClass, "enumConstantDirectory");
        blankField(enumClass, "enumConstants");
    }

    private static ConstructorAccessor getConstructorAccessor(Class<? extends java.lang.Enum<?>> enumClass, Class<?>[] additionalParameterTypes) throws NoSuchMethodException {
        final Class<?>[] parameterTypes = new Class[additionalParameterTypes.length + 2];
        parameterTypes[0] = String.class;
        parameterTypes[1] = int.class;

        System.arraycopy(additionalParameterTypes, 0, parameterTypes, 2, additionalParameterTypes.length);

        return reflectionFactory.newConstructorAccessor(enumClass.getDeclaredConstructor(parameterTypes));
    }

    private static Object makeEnum(Class<? extends java.lang.Enum<?>> enumClass, String value, int ordinal, Class<?>[] additionalTypes, Object[] additionalValues) throws NoSuchMethodException, InvocationTargetException, InstantiationException {
        final Object[] params = new Object[additionalValues.length + 2];
        params[0] = value;
        params[1] = ordinal;

        System.arraycopy(additionalValues, 0, params, 2, additionalValues.length);

        return enumClass.cast(getConstructorAccessor(enumClass, additionalTypes).newInstance(params));
    }

    private static void setAccessible(AccessibleObject accessible, boolean value) {
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            accessible.setAccessible(value);
            return null;
        });
    }
}
