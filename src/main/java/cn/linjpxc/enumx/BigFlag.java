package cn.linjpxc.enumx;

import java.math.BigInteger;

/**
 * 使用 {@link BigInteger} 表示 {@link Flag#value()}.
 *
 * @author linjpxc
 */
@SuppressWarnings({"AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc"})
public interface BigFlag<F extends Enum<F> & BigFlag<F>> extends Flag<F, BigInteger> {

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
        final BigInteger flagValue = flag.value();
        return this.value().and(flagValue).equals(flagValue);
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

        return Flag.valueOf(this.getDeclaringClass(), this.value().or(flag.value()));
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
        return Flag.valueOf(this.getDeclaringClass(), this.value().andNot(flag.value()));
    }
}
