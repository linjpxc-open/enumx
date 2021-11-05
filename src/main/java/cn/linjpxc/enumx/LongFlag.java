package cn.linjpxc.enumx;

public interface LongFlag<F extends Enum<F> & LongFlag<F>> extends Flag<F, Long> {

    @Override
    default boolean hasFlag(F flag) {
        if (flag == null) {
            return false;
        }
        final Long flagValue = flag.value();
        return (this.value() & flagValue) == flagValue;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    default F addFlag(F flag) {
        if (flag == null) {
            return (F) this;
        }

        return Flag.valueOf(this.getDeclaringClass(), this.value() | flag.value());
    }

    @Override
    @SuppressWarnings({"unchecked"})
    default F removeFlag(F flag) {
        if (flag == null) {
            return (F) this;
        }
        return Flag.valueOf(this.getDeclaringClass(), this.value() & (~flag.value()));
    }
}
