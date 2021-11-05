package cn.linjpxc.enumx;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public interface Flag<F extends Enum<F> & Flag<F, V>, V> extends EnumValue<F, V> {

    default boolean isDefined() {
        if (this instanceof Enum) {
            final Enum<?> e = (Enum<?>) this;
            return !this.value().toString().equals(e.name());
        }
        return false;
    }

    boolean hasFlag(F flag);

    F addFlag(F flag);

    F removeFlag(F flag);

    static <F extends Enum<F> & Flag<F, V>, V> F valueOf(Class<F> flagType, V value) {
        return Flags.valueOf(flagType, value);
//        final F[] values = flagType.getEnumConstants();
//        for (F f : values) {
//            if (f.value().equals(value)) {
//                return f;
//            }
//        }
//
//        try {
//            final Constructor<?>[] constructors = flagType.getDeclaredConstructors();
//            if (constructors.length != 1) {
//                throw new RuntimeException("Too many constructors.");
//            }
//
//            return Flags.addFlag(flagType, value.toString(), new Class[]{constructors[0].getParameterTypes()[2]}, new Object[]{value});
//        } catch (Exception e) {
//            throw new IllegalArgumentException("No flag constant " + flagType.getCanonicalName() + " value: " + value, e);
//        }
    }

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
