package cn.linjpxc.enumx;

public interface IntFlag<F extends Enum<F> & IntFlag<F>> extends Flag<F, Integer> {

    @Override
    default boolean hasFlag(F flag) {
        if (flag == null) {
            return false;
        }
        final int thisValue = this.value();
        final Integer flagValue = flag.value();

        return (thisValue & flagValue) == flagValue;
    }

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
