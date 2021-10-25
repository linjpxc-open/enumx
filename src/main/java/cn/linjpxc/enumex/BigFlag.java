package cn.linjpxc.enumex;

import java.math.BigInteger;

public interface BigFlag<F extends java.lang.Enum<F> & BigFlag<F>> extends Flag<F, BigFlagValue, BigInteger> {
}
