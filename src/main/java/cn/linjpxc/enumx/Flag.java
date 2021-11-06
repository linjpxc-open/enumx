package cn.linjpxc.enumx;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * 枚举值扩展接口，表示一个标识。该接口可实现动态添加枚举值。该接口描述了枚举常量间的包含关系，即一个标识可以包含多个标识。<br>
 *
 * @author linjpxc
 */
public interface Flag<F extends Enum<F> & Flag<F, V>, V> extends EnumValue<F, V> {

    /**
     * 表示该表示是已定义的，还是未定义的。静态的枚举值表示是已定义的，动态生成的枚举值表示未定义的。
     *
     * @return true 表示已定义。
     */
    default boolean isDefined() {
        if (this instanceof Enum) {
            final Enum<?> e = (Enum<?>) this;
            return !this.value().toString().equals(e.name());
        }
        return false;
    }

    /**
     * 该表示是否包含指定的标识。null 则表示未包含。
     *
     * @param flag 指定的标识，可为空
     * @return true 表示包含。
     */
    boolean hasFlag(F flag);

    /**
     * 将标识和指定的标识合并成一个新的标识，新的标识包含两者。若指定的标识为空，则不合并。
     *
     * @param flag 指定的标识。
     * @return 返回一个新的标识。
     */
    F addFlag(F flag);

    /**
     * 将标识移除指定的标识，并生成一个新的标识。若指定的标识为空，则返回当前标识。
     *
     * @param flag 指定的标识。
     * @return 返回一个新的标识，不包含指定的标识。
     */
    F removeFlag(F flag);

    /**
     * 返回指定值的标识。<br>
     * 注意：调用该方法，标识必须有一个仅带有表示值的构造函数，构造函数有且仅有一个。
     *
     * @param flagType 标识的类型class。
     * @param value    标识的值
     * @return 返回指定值的标识
     */
    static <F extends Enum<F> & Flag<F, V>, V> F valueOf(Class<F> flagType, V value) {
        return Flags.valueOf(flagType, value);
    }

    /**
     * 返回所以静态定义的标识。
     *
     * @param flagClass 标识的类型 class。
     * @return 返回所有的静态定义的标识。
     */
    @SuppressWarnings({"unchecked"})
    static <F extends Enum<F> & Flag<F, V>, V> F[] definedValues(Class<F> flagClass) {
        List<F> list = new ArrayList<>();
        for (F item : flagClass.getEnumConstants()) {
            if (item.isDefined()) {
                list.add(item);
            }
        }
        return list.toArray((F[]) Array.newInstance(flagClass, 0));
    }

    /**
     * 提供统一的标识字符串形式的简单表示。<br>
     * 建议重写 {@link Flag#toString()} ，用统一字符串表示。
     *
     * @param flag 标识
     * @return 返回标识的字符串表示
     */
    @SuppressWarnings({"unchecked"})
    static <F extends Enum<F> & Flag<F, V>, V> String toString(F flag) {
        if (flag.isDefined()) {
            return flag.name();
        }

        final F[] values = (F[]) flag.getClass().getEnumConstants();
        final StringBuilder builder = new StringBuilder();
        F f = null;
        for (F value : values) {
            if (value.isDefined()) {
                if (flag.hasFlag(value)) {
                    if (builder.length() > 0) {
                        builder.append(" | ");
                    }
                    builder.append(value.name());

                    if (f == null) {
                        f = value;
                    } else {
                        f = f.addFlag(value);
                    }

                    if (f != null && f.compareTo(flag) >= 0) {
                        break;
                    }
                }
            }
        }

        if (f == null || f != flag) {
            return flag.name();
        }
        return builder.toString();
    }
}
