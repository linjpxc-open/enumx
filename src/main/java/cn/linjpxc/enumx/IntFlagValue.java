package cn.linjpxc.enumx;

import java.util.Objects;

public final class IntFlagValue extends FlagValue<IntFlagValue, Integer> {
    private static final long serialVersionUID = 8520111187062580219L;

    private final int value;

    private IntFlagValue(int value) {
        this.value = value;
    }

    /**
     * Returns a FlagValue whose value is {@code (this & value)}.  (This
     * method returns a negative FlagValue if and only if this and val are
     * both negative.)
     *
     * @param value value to be AND'ed with this FlagValue.
     * @return {@code this & val}
     */
    @Override
    public IntFlagValue and(IntFlagValue value) {
        return new IntFlagValue(this.value & value.value);
    }

    /**
     * Returns a FlagValue whose value is {@code (this | value)}.  (This method
     * returns a negative FlagValue if and only if either this or val is
     * negative.)
     *
     * @param value value to be OR'ed with this FlagValue.
     * @return {@code this | val}
     */
    @Override
    public IntFlagValue or(IntFlagValue value) {
        return new IntFlagValue(this.value | value.value);
    }

    /**
     * Returns a FlagValue whose value is {@code (~this)}.  (This method
     * returns a negative value if and only if this FlagValue is
     * non-negative.)
     *
     * @return {@code ~this}
     */
    @Override
    public IntFlagValue not() {
        return new IntFlagValue(~this.value);
    }

    /**
     * 表示该实例的值
     *
     * @return 返回该实例的值
     */
    @Override
    public Integer value() {
        return this.value;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(IntFlagValue o) {
        return Integer.compare(this.value, o.value);
    }

    /**
     * Returns the value of the specified number as an {@code int},
     * which may involve rounding or truncation.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code int}.
     */
    @Override
    public int intValue() {
        return this.value;
    }

    /**
     * Returns the value of the specified number as a {@code long},
     * which may involve rounding or truncation.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code long}.
     */
    @Override
    public long longValue() {
        return this.value;
    }

    /**
     * Returns the value of the specified number as a {@code float},
     * which may involve rounding.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code float}.
     */
    @Override
    public float floatValue() {
        return this.value;
    }

    /**
     * Returns the value of the specified number as a {@code double},
     * which may involve rounding.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code double}.
     */
    @Override
    public double doubleValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final IntFlagValue that = (IntFlagValue) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public String toBitString() {
        return Integer.toBinaryString(this.value);
    }

    public static IntFlagValue valueOf(int value) {
        return new IntFlagValue(value);
    }

    public static int compare(IntFlagValue left, IntFlagValue right) {
        return FlagValue.compare(left, right);
    }
}
