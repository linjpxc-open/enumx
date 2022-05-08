package cn.linjpxc.enumx;

/**
 * @author linjpxc
 */
@SuppressWarnings("AlibabaAbstractClassShouldStartWithAbstractNaming")
public abstract class LongFlag<F extends LongFlag<F>> extends AbstractFlag<F, Long> {
    protected LongFlag(Long value) {
        super(value);
    }

    @Override
    public boolean hasValue(Long value) {
        if (value == null) {
            return false;
        }
        return (this.value & value) == value;
    }

    @Override
    public F addValue(Long value) {
        return createFlag(this.value | value);
    }

    @Override
    public F removeValue(Long value) {
        return createFlag(this.value & (~value));
    }

    @Override
    public int compareTo(F o) {
        if (o == null) {
            return 1;
        }
        return this.value.compareTo(o.value);
    }

    @Override
    protected final Class<?> superClass() {
        return LongFlag.class;
    }
}
