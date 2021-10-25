package cn.linjpxc.enumex;

public interface LongFlag<F extends java.lang.Enum<F> & LongFlag<F>> extends Flag<F, LongFlagValue, Long> {
}
