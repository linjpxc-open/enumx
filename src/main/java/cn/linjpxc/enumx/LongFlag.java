package cn.linjpxc.enumx;

/**
 * 使用 {@link Long} 表示 {@link Flag#value()}.
 *
 * @author linjpxc
 */
@SuppressWarnings({"AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc"})
public interface LongFlag<F extends Enum<F> & LongFlag<F>> extends Flag<F, Long> {

    /**
     * {@inheritDoc}
     *
     * @see Flag#addFlag(Enum)
     */
    @Override
    default boolean hasFlag(F flag) {
        if (flag == null) {
            return false;
        }
        final Long flagValue = flag.value();
        return (this.value() & flagValue) == flagValue;
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

        return Flag.valueOf(this.getDeclaringClass(), this.value() | flag.value());
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
        return Flag.valueOf(this.getDeclaringClass(), this.value() & (~flag.value()));
    }
}
