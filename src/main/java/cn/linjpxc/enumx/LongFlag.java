package cn.linjpxc.enumx;

/**
 * <pre>
 *     {@code
 *         public final class DemoLongFlag extends LongFlag<DemoLongFlag> {
 *
 *             @Flag(isDefined = false)
 *             public static final DemoLongFlag NONE = new DemoLongFlag(0L);
 *
 *             @Flag
 *             public static final DemoLongFlag ONE = new DemoLongFlag(1L);
 *
 *             @Flag
 *             public static final DemoLongFlag TWO = new DemoLongFlag(2L);
 *
 *             private DemoLongFlag(long value) {
 *                 super(value);
 *             }
 *
 *             private DemoLongFlag(String name, long value) {
 *                 super(name, value);
 *             }
 *
 *             @Override
 *             protected DemoLongFlag createFlag(Long value) {
 *                 return new DemoLongFlag(value);
 *             }
 *
 *             public static DemoLongFlag[] values() {
 *                 return Flags.getDefineValues(DemoLongFlag.class);
 *             }
 *
 *             public static DemoLongFlag valueOf(long value) {
 *                 return Flags.valueOf(DemoLongFlag.class, value, false);
 *             }
 *
 *             private static DemoLongFlag valueOf(String name, long value) {
 *                 return new DemoLongFlag(name, value);
 *             }
 *         }
 *     }
 * </pre>
 * @author linjpxc
 */
@SuppressWarnings("AlibabaAbstractClassShouldStartWithAbstractNaming")
public abstract class LongFlag<F extends LongFlag<F>> extends NumberFlag<F, Long> {
    protected LongFlag(long value) {
        super(value);
    }

    protected LongFlag(String name, long value) {
        super(name, value);
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
        return createFlagRemoveNone(this.value | value);
    }

    @Override
    public F removeValue(Long value) {
        return createFlagRemoveNone(this.value & (~value));
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
