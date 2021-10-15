> enumex 是对 java 枚举类型的简单扩展，通过实现 EnumValue 接口，不再局限于字符串类型

## 依赖
```xml
<dependency>
    <groupId>cn.linjpxc</groupId>
    <artifactId>enumex</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Enum 注解
@Enum 注解可以自动继承 EnumValue 接口并生成 valueOf 静态方法。枚举值为 String 时不生成 valueOf 方法，生成一个无参构造函数，value 的值为 Enum.name()

```java
import cn.linjpxc.enumex.Enum;

@Enum
public enum NumberEnum {
    ONE(1),
    TWO(2);
}
```

编译后的代码为

```java
import cn.linjpxc.enumex.EnumValue;

public enum NumberEnum implements EnumValue<NumberEnum, Integer> {
    ONE(1),
    TWO(2);

    private final Integer numberenum_value;

    private NumberEnum(Integer numberenum_value) {
        this.numberenum_value = numberenum_value;
    }

    @Override
    public Integer value() {
        return this.numberenum_value;
    }
    
    public static NumberEnum valueOf(Integer value){
        return EnumValue.valueOf(NumberEnum.class, value);
    }
}
```

值为 String 类型时

```java
import cn.linjpxc.enumex.Enum;

@Enum(valueType = String.class)
public enum StringEnum {
    
    A("a"),
    B;
}
```

编译后的代码为

```java
import cn.linjpxc.enumex.EnumValue;

public enum StringEnum implements EnumValue<StringEnum, String> {
    A("a"),
    B;

    private final String stringenum_value;

    private StringEnum() {
        this.stringenum_value = this.name();
    }

    private StringEnum(String stringenum_value) {
        this.stringenum_value = stringenum_value;
    }

    @Override
    public String value() {
        return this.stringenum_value;
    }
}
```
