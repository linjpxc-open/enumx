package cn.linjpxc.enumx;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author linjpxc
 */
public interface FlagValue<F extends FlagValue<F, V>, V> extends Valuable<V>, Comparable<F> {

    /**
     * name
     *
     * @return name
     */
    default String name() {
        final List<FlagWrapper<F, V>> list = Flags.getFlagWrappers(this.getDeclaringClass());
        for (FlagWrapper<F, V> item : list) {
            if (item.getValue().equals(this)) {
                return item.getName();
            }
        }
        return this.value().toString();
    }

    /**
     * 是否默认定义的
     *
     * @return 定义的
     */
    default boolean isDefined() {
        final List<FlagWrapper<F, V>> list = Flags.getFlagWrappers(this.getDeclaringClass());
        for (FlagWrapper<F, V> item : list) {
            if (item.getValue().equals(this)) {
                return item.getFlag().isDefined();
            }
        }
        return false;
    }

    /**
     * 该表示是否包含指定的标识。null 则表示未包含。
     *
     * @param flag 指定的标识，可为空
     * @return true 表示包含。
     */
    default boolean hasFlag(F flag) {
        if (flag == null) {
            return false;
        }
        return this.hasValue(flag.value());
    }

    /**
     * hasValue
     *
     * @param value hasValue
     * @return true
     */
    boolean hasValue(V value);

    /**
     * 将标识和指定的标识合并成一个新的标识，新的标识包含两者。若指定的标识为空，则不合并。
     *
     * @param flag 指定的标识。
     * @return 返回一个新的标识。
     */
    default F addFlag(F flag) {
        return this.addValue(flag.value());
    }

    /**
     * addValue
     *
     * @param value value
     * @return true
     */
    F addValue(V value);

    /**
     * 将标识移除指定的标识，并生成一个新的标识。若指定的标识为空，则返回当前标识。
     *
     * @param flag 指定的标识。
     * @return 返回一个新的标识，不包含指定的标识。
     */
    default F removeFlag(F flag) {
        return this.removeValue(flag.value());
    }

    /**
     * removeValue
     *
     * @param value value
     * @return this
     */
    F removeValue(V value);

    /**
     * declaring class
     *
     * @return declaring class
     */
    Class<F> getDeclaringClass();

    default F[] toDefineArray() {
        return toDefineArray(false);
    }

    /**
     * toDefineArray
     *
     * @return toDefineArray
     */
    @SuppressWarnings({"unchecked"})
    default F[] toDefineArray(boolean withOther) {
        final List<F> list = toDefineFlags(withOther);
        final F[] arr = (F[]) Array.newInstance(this.getDeclaringClass(), 0);
        if (list.isEmpty()) {
            return arr;
        }
        return list.toArray(arr);
    }

    default List<F> toDefineFlags() {
        return this.toDefineFlags(false);
    }

    /**
     * toDefineFlags
     *
     * @return toDefineFlags
     */
    @SuppressWarnings({"unchecked"})
    default List<F> toDefineFlags(boolean withOther) {
        final Class<F> declaringClass = this.getDeclaringClass();
        final F[] defineValues = Flags.getDefineValues(declaringClass);
        final List<F> list = new ArrayList<>();
        F f = null;
        for (F item : defineValues) {
            if (this.hasFlag(item)) {
                list.add(item);
                if (f == null) {
                    f = item;
                } else {
                    f = f.addFlag(item);
                }
            }
        }
        if (withOther) {
            if (!this.equals(f)) {
                if (f == null) {
                    list.add((F) this);
                } else {
                    list.add(this.removeFlag(f));
                }
            }
        }

        return Collections.unmodifiableList(list);
    }

    /**
     * valueOf
     *
     * @param clazz clazz
     * @param value value
     * @param <F>   F
     * @param <V>   V
     * @return Flag
     */
    static <F extends FlagValue<F, V>, V> F valueOf(Class<F> clazz, V value) {
        return Flags.valueOf(clazz, value);
    }

    static <F extends FlagValue<F, V>, V> int compare(F left, F right) {
        if (left == right) {
            return 0;
        }
        if (left == null) {
            return -1;
        }
        return left.compareTo(right);
    }
}
