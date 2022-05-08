package cn.linjpxc.enumx;

import java.util.Objects;

/**
 * @author linjpxc
 */
public abstract class AbstractFlag<F extends AbstractFlag<F, V>, V> implements FlagValue<F, V> {
    private static final long serialVersionUID = 8938203086618420260L;

    protected final V value;

    protected AbstractFlag(V value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public V value() {
        return this.value;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public final Class<F> getDeclaringClass() {
        final Class<?> clazz = getClass();
        final Class<?> zuper = clazz.getSuperclass();
        return (zuper == this.superClass()) ? (Class<F>) clazz : (Class<F>) zuper;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof FlagValue<?, ?> && this.getDeclaringClass() == ((FlagValue<?, ?>) obj).getDeclaringClass()) {
            final AbstractFlag<F, V> that = (AbstractFlag<F, V>) obj;
            return Objects.equals(this.value(), that.value());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value());
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public String toString() {
        return Flags.toString((F) this);
    }

    /**
     * 创建 Flag
     *
     * @param value flag
     * @return this
     */
    protected abstract F createFlag(V value);

    /**
     * super class
     * @return super class
     */
    protected abstract Class<?> superClass();
}
