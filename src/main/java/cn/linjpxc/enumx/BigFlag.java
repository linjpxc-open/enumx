package cn.linjpxc.enumx;

import java.math.BigInteger;

public interface BigFlag<F extends Enum<F> & BigFlag<F>> extends Flag<F, BigFlagValue, BigInteger> {
}
