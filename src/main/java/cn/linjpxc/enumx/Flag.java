package cn.linjpxc.enumx;

import java.lang.annotation.*;


/**
 * @author linjpxc
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Flag {

    boolean isDefined() default true;
}
