package cn.linjpxc.enumx;

import java.io.Serializable;
import java.lang.reflect.Method;

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

    @SuppressWarnings({"unchecked"})
    default Class<V> valueType() {
        try {
            final Method method = this.getClass().getDeclaredMethod("value");
            return (Class<V>) method.getReturnType();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
