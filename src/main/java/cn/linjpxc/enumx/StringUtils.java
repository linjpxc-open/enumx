package cn.linjpxc.enumx;

/**
 * @author linjpxc
 */
final class StringUtils {

    private StringUtils(){}

    static boolean isEmpty(String value){
        return value == null || value.isEmpty();
    }

    static boolean isNotEmpty(String value){
        return !isEmpty(value);
    }

    static boolean isBlank(String value){
        return value == null || value.trim().isEmpty();
    }

    static boolean isNotBlank(String value){
        return !isBlank(value);
    }
}
