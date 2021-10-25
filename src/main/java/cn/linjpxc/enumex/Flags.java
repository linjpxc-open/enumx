//package cn.linjpxc.enumex;
//
//import java.lang.annotation.*;
//
//@Inherited
//@Documented
//@Target({ElementType.TYPE})
//@Retention(RetentionPolicy.SOURCE)
//public @interface Flags {
//
//    FlagType flagType() default FlagType.INT_FLAG;
//
//    String valueFieldName() default "value";
//
//    enum FlagType implements EnumValue<FlagType, Integer> {
//
//        INT_FLAG(1),
//
//        LONG_FLAG(2),
//
//        BIG_FLAG(3),
//
//        OTHER_FLAG(4);
//
//        private final int value;
//
//        FlagType(int value) {
//            this.value = value;
//        }
//
//        /**
//         * 表示枚举值。
//         */
//        @Override
//        public Integer value() {
//            return this.value;
//        }
//
//        public static FlagType valueOf(int value) {
//            return EnumValue.valueOf(FlagType.class, value);
//        }
//    }
//}
