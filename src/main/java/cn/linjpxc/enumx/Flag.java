package cn.linjpxc.enumx;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public interface Flag<F extends Enum<F> & Flag<F, FV, V>, FV extends FlagValue<FV, V>, V extends Number> extends EnumValue<F, V> {

    FV flagValue();

    @Override
    default V value() {
        return this.flagValue().value();
    }

    default boolean isDefined() {
        if (this instanceof Enum) {
            final Enum<?> e = (Enum<?>) this;
            return !this.value().toString().equals(e.name());
        }
        return false;
    }

    default boolean hasFlag(F flag) {
        return this.flagValue().and(flag.flagValue()).equals(flag.flagValue());
    }

    @SuppressWarnings({"unchecked"})
    default F addFlag(F flag) {
        return valueOf((Class<F>) this.getClass(), this.flagValue().or(flag.flagValue()));
    }

    @SuppressWarnings({"unchecked"})
    default F removeFlag(F flag) {
        return valueOf((Class<F>) this.getClass(), this.flagValue().andNot(flag.flagValue()));
    }

    static <F extends Enum<F> & Flag<F, FV, V>, FV extends FlagValue<FV, V>, V extends Number> F valueOf(Class<F> flagClass, V value) {
        final F[] values = flagClass.getEnumConstants();
        for (F f : values) {
            if (f.value().equals(value)) {
                return f;
            }
        }

        try {
            boolean isPrimitive = false;
            final Constructor<?>[] constructors = flagClass.getDeclaredConstructors();
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterTypes()[2].isPrimitive()) {
                    isPrimitive = true;
                    break;
                }
            }

            return FlagUtil.addFlag(flagClass, value.toString(), new Class[]{FlagUtil.getFlagValueClass(flagClass, isPrimitive)}, new Object[]{value});
        } catch (Exception e) {
            throw new IllegalArgumentException("No flag constant " + flagClass.getCanonicalName() + " value: " + value, e);
        }
    }

    static <F extends Enum<F> & Flag<F, FV, V>, FV extends FlagValue<FV, V>, V extends Number> F valueOf(Class<F> flagClass, FV value) {
        return valueOf(flagClass, value.value());
    }

    @SuppressWarnings({"unchecked"})
    static <F extends Enum<F> & Flag<F, FV, V>, FV extends FlagValue<FV, V>, V extends Number> F[] definedValues(Class<F> flagClass) {
        List<F> list = new ArrayList<>();
        for (F item : flagClass.getEnumConstants()) {
            if (item.isDefined()) {
                list.add(item);
            }
        }
        return list.toArray((F[]) Array.newInstance(flagClass, 0));
    }

    @SuppressWarnings({"unchecked"})
    static <F extends Enum<F> & Flag<F, FV, V>, FV extends FlagValue<FV, V>, V extends Number> String toString(F flag) {
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
