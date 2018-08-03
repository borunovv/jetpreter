package com.borunovv.jetpreter.core.util;

/**
 * @author borunovv
 */
public final class StringUtils {

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String ensureString(String source) {
        return source != null ?
                source :
                "";
    }
}
