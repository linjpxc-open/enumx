package cn.linjpxc.enumx;

import java.math.BigInteger;

public interface BigFlag<F extends Enum<F> & BigFlag<F>> extends Flag<F, BigInteger> {

    @Override
    default boolean hasFlag(F flag) {
        if (flag == null) {
            return false;
        }
        final BigInteger flagValue = flag.value();
        return this.value().and(flagValue).equals(flagValue);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    default F addFlag(F flag) {
        if (flag == null) {
            return (F) this;
        }

        return Flag.valueOf(this.getDeclaringClass(), this.value().or(flag.value()));
    }

    @Override
    @SuppressWarnings({"unchecked"})
    default F removeFlag(F flag) {
        if (flag == null) {
            return (F) this;
        }
        return Flag.valueOf(this.getDeclaringClass(), this.value().andNot(flag.value()));
    }
}
