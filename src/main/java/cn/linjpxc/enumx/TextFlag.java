package cn.linjpxc.enumx;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author linjpxc
 */
@SuppressWarnings("AlibabaAbstractClassShouldStartWithAbstractNaming")
public abstract class TextFlag<F extends TextFlag<F>> extends AbstractFlag<F, String> {

    protected static final String DEFAULT_DELIMITER = "|";

    private final String delimiter;
    private final String splitDelimiter;

    protected TextFlag(String delimiter) {
        super("");
        if (delimiter.length() < 1) {
            throw new IllegalArgumentException("Delimiter is empty.");
        }
        this.delimiter = delimiter;
        this.splitDelimiter = "\\" + delimiter;
    }

    protected TextFlag(String delimiter, String value) {
        super(handleValue(value, delimiter).toUpperCase(Locale.ROOT));
        if (delimiter.length() < 1) {
            throw new IllegalArgumentException("Delimiter is empty.");
        }
        this.delimiter = delimiter;
        this.splitDelimiter = "\\" + delimiter;
    }

    @Override
    protected Class<?> superClass() {
        return TextFlag.class;
    }

    @Override
    public String value() {
        if (this.value.length() < 1) {
            Flags.getFlagWrappers(this.getDeclaringClass());
        }
        return super.value();
    }

    @Override
    public boolean hasValue(String value) {
        if (value == null) {
            return false;
        }
        value = this.handleValue(value);
        if (value.length() < 1) {
            return this.hasEmpty();
        }
        final String thisValue = this.value();
        if (thisValue.equalsIgnoreCase(value)) {
            return true;
        }

        final String[] thisArray = thisValue.split(this.splitDelimiter);
        final String[] valueArray = value.split(this.splitDelimiter);

        valueLabel:
        for (String valueItem : valueArray) {
            valueItem = valueItem.trim();
            if (valueItem.length() > 0) {
                for (String thisItem : thisArray) {
                    thisItem = thisItem.trim();
                    if (thisItem.equalsIgnoreCase(valueItem)) {
                        continue valueLabel;
                    }
                }
                return false;
            }
        }

        return true;
    }

    @Override
    public F addValue(String value) {
        value = this.handleValue(value);

        final String[] thisArray = this.value().split(this.splitDelimiter);
        final String[] valueArray = value.split(this.splitDelimiter);

        final StringBuilder builder = new StringBuilder(this.value);
        valueLabel:
        for (String valueItem : valueArray) {
            valueItem = valueItem.trim();
            if (valueItem.length() > 0) {
                for (String thisItem : thisArray) {
                    if (thisItem.equalsIgnoreCase(valueItem)) {
                        continue valueLabel;
                    }
                }
                if (builder.length() > 0) {
                    builder.append(this.delimiter);
                }
                builder.append(valueItem);
            }
        }
        return createFlag(builder.toString());
    }

    @Override
    public F removeValue(String value) {
        value = this.handleValue(value);

        final String[] thisArray = this.value().split(this.splitDelimiter);
        final String[] valueArray = value.split(this.splitDelimiter);

        final StringBuilder builder = new StringBuilder();

        thisLabel:
        for (String thisItem : thisArray) {
            for (String valueItem : valueArray) {
                valueItem = valueItem.trim();
                if (valueItem.length() > 0) {
                    if (thisItem.equalsIgnoreCase(valueItem)) {
                        continue thisLabel;
                    }
                }
            }
            if (builder.length() > 0) {
                builder.append(this.delimiter);
            }
            builder.append(thisItem);
        }

        return createFlag(builder.toString());
    }

    @Override
    public int compareTo(F o) {
        if (o == null) {
            return 1;
        }
        return this.value.compareTo(o.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof FlagValue<?, ?> && this.getDeclaringClass() == ((FlagValue<?, ?>) obj).getDeclaringClass()) {
            final TextFlag<?> that = (TextFlag<?>) obj;
            return this.value().equalsIgnoreCase(that.value());
        }
        return false;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public String toString() {
        return Flags.toString((F) this, this.delimiter);
    }

    protected boolean hasEmpty() {
        return false;
    }

    protected final String handleValue(String value) {
        return handleValue(value, this.delimiter);
    }

    private static String handleValue(String value, String delimiter) {
        value = value.trim();
        if (value.startsWith(delimiter)) {
            value = value.substring(1);
        }
        if (value.endsWith(delimiter)) {
            value = value.substring(0, value.length() - 1);
        }
        value = value.trim();

        final String[] array = value.split("\\" + delimiter);
        final Set<String> set = new TreeSet<>((o1, o2) -> {
            if (Objects.equals(o1, o2)) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            return o1.compareToIgnoreCase(o2);
        });
        final StringBuilder builder = new StringBuilder();
        for (String item : array) {
            item = item.trim();
            if (set.add(item)) {
                if (builder.length() > 0) {
                    builder.append(delimiter);
                }
                builder.append(item);
            }
        }
        return builder.toString();
    }
}
