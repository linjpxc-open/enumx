package cn.linjpxc.enumx;

public interface LongFlag<F extends java.lang.Enum<F> & LongFlag<F>> extends Flag<F, LongFlagValue, Long> {
}
