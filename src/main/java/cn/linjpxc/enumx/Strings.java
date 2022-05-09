package cn.linjpxc.enumx;

/**
 * @author linjpxc
 */
final class Strings {

    private Strings(){}

    static boolean isEmpty(String value){
        return value == null || value.length() < 1;
    }

    static boolean isNotEmpty(String value){
        return !isEmpty(value);
    }

    static boolean isBlank(String value){
        return value == null || value.trim().length() < 1;
    }

    static boolean isNotBlank(String value){
        return !isBlank(value);
    }
}
