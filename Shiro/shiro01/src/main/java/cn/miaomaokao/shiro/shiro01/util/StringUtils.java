package cn.miaomaokao.shiro.shiro01.util;

/**
 * @Author miaomaokao
 * @create 2019/8/3 11:11
 */
public class StringUtils {
    /**
     * str 非空校验
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isEmpty(final String str) {
        return (null == str) || "".equals(str) || 0 == str.length();
    }

    public static boolean isNotEmpty(final String str){
        return (null != str) && "".equals(str) && 0 == str.length();
    }
}
