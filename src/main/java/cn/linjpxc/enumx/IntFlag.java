package cn.linjpxc.enumx;

/**
 * 使用 {@link Integer} 表示 {@link Flag#value()}.
 *
 * @author linjpxc
 */
@SuppressWarnings({"AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc"})
public interface IntFlag<F extends Enum<F> & IntFlag<F>> extends Flag<F, Integer> {

    /**
     * {@inheritDoc}
     *
     * @see Flag#hasFlag(Enum)
     */
    @Override
    default boolean hasFlag(F flag) {
        if (flag == null) {
            return false;
        }
        final int thisValue = this.value();
        final Integer flagValue = flag.value();

        return (thisValue & flagValue) == flagValue;
    }

    /**
     * {@inheritDoc}
     *
     * @see Flag#addFlag(Enum)
     */
    @Override
    @SuppressWarnings({"unchecked"})
    default F addFlag(F flag) {
        if (flag == null) {
            return (F) this;
        }
        final int thisValue = this.value();
        final int flagValue = flag.value();

        return Flag.valueOf(this.getDeclaringClass(), thisValue | flagValue);
    }

    /**
     * {@inheritDoc}
     *
     * @see Flag#removeFlag(Enum)
     */
    @Override
    @SuppressWarnings({"unchecked"})
    default F removeFlag(F flag) {
        if (flag == null) {
            return (F) this;
        }
        final int thisValue = this.value();
        final int flagValue = flag.value();

        return Flag.valueOf(this.getDeclaringClass(), thisValue & (~flagValue));
    }
}
