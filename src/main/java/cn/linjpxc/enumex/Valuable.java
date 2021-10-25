package cn.linjpxc.enumex;

import java.io.Serializable;

/**
 * 表示单个值的接口
 *
 * @param <V> 值类型
 */
public interface Valuable<V> extends Serializable {

    /**
     * 表示该实例的值
     *
     * @return 返回该实例的值
     */
    V value();
}
