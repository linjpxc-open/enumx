> enumex 是对 java 枚举类型的简单扩展，通过实现 EnumValue 接口，不再局限于字符串类型

## 依赖

```xml

<dependency>
    <groupId>cn.linjpxc</groupId>
    <artifactId>enumx</artifactId>
    <version>0.2.2</version>
</dependency>
```

## Enum 注解

@Enum 注解可以自动继承 EnumValue 接口并生成 valueOf 静态方法。枚举值为 String 时不生成 valueOf 方法，生成一个无参构造函数，value 的值为 Enum.name(). 字段的 name 可通过
valueFieldName 设置，默认为 value.

```java
import cn.linjpxc.enumx.Enumerable;

@Enumerable
public enum NumberEnum {
    ONE(1),
    TWO(2);
}
```

编译后的代码为

```java
import cn.linjpxc.enumx.EnumValue;

public enum NumberEnum implements EnumValue<NumberEnum, Integer> {
    ONE(1),
    TWO(2);

    private final Integer value;

    private NumberEnum(Integer value) {
        this.value = value;
    }

    @Override
    public Integer value() {
        return this.value;
    }

    public static NumberEnum valueOf(Integer value) {
        return EnumValue.valueOf(NumberEnum.class, value);
    }
}
```

值为 String 类型时

```java
import cn.linjpxc.enumx.Enumerable;

@Enumerable(valueType = String.class)
public enum StringEnum {

    A("a"),
    B;
}
```

编译后的代码为

```java
import cn.linjpxc.enumx.EnumValue;

public enum StringEnum implements EnumValue<StringEnum, String> {
    A("a"),
    B;

    private final String value;

    private StringEnum() {
        this.stringenum_value = this.name();
    }

    private StringEnum(String value) {
        this.stringenum_value = stringenum_value;
    }

    @Override
    public String value() {
        return this.value;
    }
}
```

## Flag(实验)

Flag 实现了动态 enum，做为 enum 功能的扩展。Flag 可以将 enum 视为位字段，表示一组标识.

用法

```java
import cn.linjpxc.enumx.Flag;
import cn.linjpxc.enumx.IntFlag;
import cn.linjpxc.enumx.IntFlagValue;

public enum Role implements IntFlag<Role> {

    A(1),

    B(2);

    private final IntFlagValue flagValue;

    Role(int value) {
        this.flagValue = IntFlagValue.valueOf(value);
    }

    @Override
    public IntFlagValue flagValue() {
        return this.flagValue;
    }

    @Override
    public String toString() {
        return Flag.toString(this);
    }

    public static Role valueOf(int value) {
        return Flag.valueOf(Role.class, value);
    }
}
```

上述代码为一个角色标识，当拥有多个角色时，可表示为

```java
Role aAndB=Role.A.addFlag();
```

## Enums 提供一些枚举相关的工具方法

可通过 EnumValue 的值或者通过 Enum 的名称获取枚举值，当 EnumValue 值为基本类型时，会进行适当的转换，如 EnumValue 的值类型时 Integer，value = "1"，会转换成数字 1 进行处理。
例如：

```java
import cn.linjpxc.enumx.EnumValue;
import cn.linjpxc.enumx.Enumerable;

@Enumerable(valueType = Integer.class)
enum NumberEnum implements EnumValue<NumberEnum, Integer> {
    ONE(1),
    TWO(2);
}
```

Emums.valueOf 方法
```java
NumberEnum one = Enums.valueOf(NumberEnum.class, "1");
NumberEnum one = Enums.valueOf(NumberEnum.class, "one");
```

Enums.valueOfIgnoreCase 方法可以忽略大小写，判断枚举值是否存在
```java
NumberEnum one = Enums.valueOfIgnoreCase(NumberEnum.class, "one");
```

Enums.exists 方法可以检测当前枚举值是否存在
```java
boolean false = Enums.exists(NumberEnum.class, "one");
boolean true = Enums.exists(NumberEnum.class, "ONE");
```
