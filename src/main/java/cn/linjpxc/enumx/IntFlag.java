package cn.linjpxc.enumx;

/**
 * <pre>
 *     {@code
 *         public final class DemoIntFlag extends IntFlag<DemoIntFlag> {
 *
 *             @Flag(isDefined = false)
 *             public static final DemoIntFlag NONE = new DemoIntFlag(0);
 *
 *             @Flag
 *             public static final DemoIntFlag ONE = new DemoIntFlag(1);
 *
 *             @Flag
 *             public static final DemoIntFlag TWO = new DemoIntFlag(2);
 *
 *             private DemoIntFlag(int value) {
 *                 super(value);
 *             }
 *
 *             private DemoIntFlag(String name, int value) {
 *                 super(name, value);
 *             }
 *
 *             @Override
 *             protected DemoIntFlag createFlag(Integer value) {
 *                 return new DemoIntFlag(value);
 *             }
 *
 *             public static DemoIntFlag[] values() {
 *                 return Flags.getDefineValues(DemoIntFlag.class);
 *             }
 *
 *             public static DemoIntFlag valueOf(int value) {
 *                 return Flags.valueOf(DemoIntFlag.class, value, false);
 *             }
 *
 *             private static DemoIntFlag valueOf(String name, int value) {
 *                 return new DemoIntFlag(name, value);
 *             }
 *         }
 *     }
 * </pre>
 *
 * @author linjpxc
 */
@SuppressWarnings("AlibabaAbstractClassShouldStartWithAbstractNaming")
public abstract class IntFlag<F extends IntFlag<F>> extends NumberFlag<F, Integer> {
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
        return createFlagRemoveNone(this.value() | value);
    }

    @Override
    public F removeValue(Integer value) {
        return createFlagRemoveNone(this.value() & (~value));
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
