package cn.linjpxc.enumx;

/**
 * @author linjpxc
 */
abstract class NumberFlag<F extends AbstractFlag<F, V>, V extends Number> extends AbstractFlag<F, V> {
    protected NumberFlag(V value) {
        super(value);
    }

    protected NumberFlag(String name, V value) {
        super(name, value);
    }

    @Override
    protected F createFlagRemoveNone(V value) {
        final F noneFlag = this.noneFlag();
        if (noneFlag != null && noneFlag.value.intValue() == 0) {
            return createFlag(value);
        }

        return super.createFlagRemoveNone(value);
    }
}
