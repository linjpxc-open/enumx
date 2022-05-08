package cn.linjpxc.enumx;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author linjpxc
 */
final class FlagWrapper<F extends FlagValue<F, V>, V> implements Serializable {
    private static final long serialVersionUID = -2534699961159475040L;

    private final Flag flag;
    private final String name;
    private final FlagValue<F, V> value;

    FlagWrapper(Flag flag, String name, FlagValue<F, V> value) {
        this.flag = Objects.requireNonNull(flag);
        this.name = Objects.requireNonNull(name);
        this.value = Objects.requireNonNull(value);
    }

    public Flag getFlag() {
        return flag;
    }

    public String getName() {
        return name;
    }

    public FlagValue<F, V> getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FlagWrapper)) {
            return false;
        }
        final FlagWrapper<?, ?> that = (FlagWrapper<?, ?>) o;
        return Objects.equals(getFlag(), that.getFlag()) && Objects.equals(getName(), that.getName()) && Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFlag(), getName(), getValue());
    }
}
