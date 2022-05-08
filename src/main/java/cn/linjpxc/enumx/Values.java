package cn.linjpxc.enumx;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author linjpxc
 */
public final class Values {
    private Values() {
    }

    private static final ConcurrentMap<Class<?>, Class<?>> VALUE_TYPES = new ConcurrentHashMap<>();

    @SuppressWarnings({"unchecked"})
    public static <T extends Valuable<V>, V> Class<V> getValueType(Class<T> type) {
        if (!Valuable.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("type not is Valuable.");
        }

        return (Class<V>) VALUE_TYPES.computeIfAbsent(type, key -> {
            final Type valueType = getValueTypeArgument(getParentType(type));
            if (!(valueType instanceof Class<?>)) {
                throw new IllegalStateException();
            }
            return (Class<?>) valueType;
        });
    }

    private static Type getParentType(Class<?> type) {
        final Type[] genericInterfaces = type.getGenericInterfaces();
        for (Type item : genericInterfaces) {
            Class<?> midClass;
            if (item instanceof ParameterizedType) {
                final Type rawType = ((ParameterizedType) item).getRawType();
                if (!(rawType instanceof Class<?>)) {
                    throw new IllegalStateException("");
                }
                midClass = (Class<?>) rawType;
            } else if (item instanceof Class<?>) {
                midClass = (Class<?>) item;
            } else {
                throw new IllegalStateException();
            }

            if (Valuable.class.isAssignableFrom(midClass)) {
                return item;
            }
        }

        return type.getGenericSuperclass();
    }

    private static Type getValueTypeArgument(Type type) {
        if (type instanceof Class<?>) {
            return getValueTypeArgument((Class<?>) type);
        }
        if (type instanceof ParameterizedType) {
            return getValueTypeArgument((ParameterizedType) type);
        }
        return null;
    }

    private static Type getValueTypeArgument(ParameterizedType parameterizedType) {
        final Type rawType = parameterizedType.getRawType();
        if (!(rawType instanceof Class<?>)) {
            throw new IllegalStateException("");
        }
        if (rawType == Valuable.class) {
            return parameterizedType.getActualTypeArguments()[0];
        }
        if (!Valuable.class.isAssignableFrom((Class<?>) rawType)) {
            return null;
        }

        final Type valueTypeArgument = getValueTypeArgument(rawType);
        if (valueTypeArgument instanceof ParameterizedType) {
            return parameterizedType.getActualTypeArguments()[0];
        }
        if (valueTypeArgument instanceof TypeVariable) {
            return getValueTypeArgument(parameterizedType, (TypeVariable<?>) valueTypeArgument);
        }
        if (valueTypeArgument instanceof Class<?>) {
            return valueTypeArgument;
        }
        return null;
    }

    private static Type getValueTypeArgument(ParameterizedType parameterizedType, TypeVariable<?> typeVariable) {
        final Class<?> rawType = (Class<?>) parameterizedType.getRawType();
        final TypeVariable<? extends Class<?>>[] typeParameters = rawType.getTypeParameters();

        for (int i = 0; i < typeParameters.length; i++) {
            if (typeParameters[i] == typeVariable) {
                return parameterizedType.getActualTypeArguments()[i];
            }
        }
        return null;
    }

    private static Type getValueTypeArgument(Class<?> type) {
        if (!Valuable.class.isAssignableFrom(type)) {
            return null;
        }

        return getValueTypeArgument(getParentType(type));
    }

    private static Class<?> getRawType(ParameterizedType parameterizedType) {
        final Type rawType = parameterizedType.getRawType();
        if (rawType instanceof Class<?>) {
            return (Class<?>) rawType;
        }
        throw new IllegalStateException("");
    }

}
