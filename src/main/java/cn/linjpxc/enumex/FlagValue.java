package cn.linjpxc.enumex;

public abstract class FlagValue<T extends FlagValue<T, V>, V extends Number> extends Number implements Comparable<T>, Valuable<V> {

    /**
     * Returns a FlagValue whose value is {@code (this & value)}.  (This
     * method returns a negative FlagValue if and only if this and val are
     * both negative.)
     *
     * @param value value to be AND'ed with this FlagValue.
     * @return {@code this & val}
     */
    public abstract T and(T value);

    /**
     * Returns a FlagValue whose value is {@code (this | value)}.  (This method
     * returns a negative FlagValue if and only if either this or val is
     * negative.)
     *
     * @param value value to be OR'ed with this FlagValue.
     * @return {@code this | val}
     */
    public abstract T or(T value);

    /**
     * Returns a FlagValue whose value is {@code (~this)}.  (This method
     * returns a negative value if and only if this FlagValue is
     * non-negative.)
     *
     * @return {@code ~this}
     */
    public abstract T not();

    /**
     * Returns a FlagValue whose value is {@code (this & ~value)}.  This
     * method, which is equivalent to {@code and(value.not())}, is provided as
     * a convenience for masking operations.  (This method returns a negative
     * FlagValue if and only if {@code this} is negative and {@code value} is
     * positive.)
     *
     * @param value value to be complemented and AND'ed with this FlagValue.
     * @return {@code this & ~value}
     */
    public T andNot(T value) {
        return this.and(value.not());
    }

    public abstract String toBitString();

    protected static <F extends FlagValue<F, V>, V extends Number> int compare(F left, F right) {
        if (left == null) {
            return right == null ? 0 : -1;
        }
        return right == null ? 1 : left.compareTo(right);
    }
}
