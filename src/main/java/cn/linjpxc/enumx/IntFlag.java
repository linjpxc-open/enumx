package cn.linjpxc.enumx;

public interface IntFlag<F extends java.lang.Enum<F> & IntFlag<F>> extends Flag<F, IntFlagValue, Integer> {
}
