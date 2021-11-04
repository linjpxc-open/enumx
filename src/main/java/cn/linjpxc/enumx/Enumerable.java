package cn.linjpxc.enumx;

import java.lang.annotation.*;

/**
 * 可自动生成枚举代码
 */
@Inherited
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Enumerable {

    /**
     * 枚举值类型
     */
    Class<?> valueType() default Integer.class;

    /**
     * 枚举值字段名
     */
    String valueFieldName() default "value";
}
