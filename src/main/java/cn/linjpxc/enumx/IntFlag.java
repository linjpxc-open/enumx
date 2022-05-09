package cn.linjpxc.enumx;

/**
 * @author linjpxc
 */
@SuppressWarnings("AlibabaAbstractClassShouldStartWithAbstractNaming")
public abstract class IntFlag<F extends IntFlag<F>> extends AbstractFlag<F, Integer> {
    protected IntFlag(int value) {
        super(value);
    }

    protected IntFlag(String name, int value) {
        super(name, value);
    }

    @Override
    public boolean hasValue(Integer value) {
        if (value == null) {
            return false;
        }
        return (this.value() & value) == value;
    }

    @Override
    public F addValue(Integer value) {
        return createFlag(this.value() | value);
    }

    @Override
    public F removeValue(Integer value) {
        return createFlag(this.value() & (~value));
    }

    @Override
    public int compareTo(F o) {
        if (o == null) {
            return 1;
        }
        return this.value().compareTo(o.value());
    }

    @Override
    protected final Class<?> superClass() {
        return IntFlag.class;
    }
}
