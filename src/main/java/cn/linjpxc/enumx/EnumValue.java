package cn.linjpxc.enumx;

import java.util.Objects;

/**
 * 统一枚举值接口。
 *
 * @param <E> 枚举类型
 * @param <V> 枚举值类型
 * @author linjpxc
 */
@SuppressWarnings({"AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc"})
public interface EnumValue<E extends java.lang.Enum<E>, V> extends Valuable<V> {

    /**
     * {@inheritDoc}
     *
     * @see Enum#getDeclaringClass()
     */
    Class<E> getDeclaringClass();

    /**
     * Returns the enum constant of the specified enum type with the
     * specified value.
     *
     * @param <E>      The enum type whose constant is to be returned
     * @param enumType the {@code Class} object of the enum type from which
     *                 to return a constant
     * @param value    the value of the constant to return
     * @return the enum constant of the specified enum type with the
     * specified value
     * @throws IllegalArgumentException if the specified enum type has
     *                                  no constant with the specified value, or the specified
     *                                  class object does not represent an enum type
     * @throws NullPointerException     if {@code enumType} or {@code name}
     *                                  is null
     */
    static <E extends Enum<E> & EnumValue<E, V>, V> E valueOf(Class<E> enumType, V value) {
        Objects.requireNonNull(value, "Value is null");
        final E[] values = enumType.getEnumConstants();
        for (E item : values) {
            if (Objects.equals(item.value(), value)) {
                return item;
            }
        }
        throw new IllegalArgumentException("No enum constant " + enumType.getCanonicalName() + " value: " + value);
    }

    /**
     * 提供实例值比较。若实例的值实现 {@link Comparable} 接口，则比较使用实例值比较，否则使用枚举名称比较 {@link Enum#compareTo(Enum)}
     *
     * @param left  left
     * @param right right
     * @param <E>   枚举类型
     * @param <V>   枚举值类型
     * @return {@link Comparable#compareTo(Object)}
     */
    @SuppressWarnings({"unchecked"})
    static <E extends Enum<E> & EnumValue<E, V>, V> int compare(E left, E right) {
        if (left == right) {
            return 0;
        }
        if (left == null) {
            return -1;
        }
        if (right == null) {
            return 1;
        }
        try {
            final Class<?> valueType = left.getClass().getDeclaredMethod("value").getReturnType();
            if (Comparable.class.isAssignableFrom(valueType)) {
                final Comparable<Object> comparable = (Comparable<Object>) left.value();
                return comparable.compareTo(right.value());
            }
        } catch (Exception ignored) {
        }
        return left.compareTo(right);
    }
}
