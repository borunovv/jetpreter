package com.borunovv.jetpreter.core.util;

import com.borunovv.jetpreter.core.contract.Precondition;

import java.util.List;
import java.util.Map;

/**
 * @author borunovv
 */
public final class CollectionUtils {

    public static String mapToString(Map<?, ?> map) {
       return mapToString(map, ";", "");
    }

    public static String mapToString(Map<?, ?> map, String pairDelimiter, String offsetDelimiter) {
        StringBuilder sb = new StringBuilder();
        mapToString(sb, map, pairDelimiter, offsetDelimiter, 0);
        return sb.toString();
    }

    public static String toCommaSeparatedList(List<?> items) {
        return toSeparatedList(items, ",");
    }

    public static String toSeparatedList(List<?> items, String separator) {
        Precondition.expected(items != null, "Bad items: null");
        Precondition.expected(separator != null, "Bad separator: null");

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object item : items) {
            if (item != null) {
                if (!first) {
                    sb.append(separator);
                }
                sb.append(item);
                first = false;
            }
        }

        return sb.toString();
    }

    private static void mapToString(StringBuilder sb,
                                    Map<?, ?> map,
                                    String pairDelimiter,
                                    String offsetDelimiter,
                                    int level) {
        if (map == null) {
            sb.append("null");
            return;
        }

        String prefix = "";
        for (int i = 0; i < level; ++i) {
            prefix += offsetDelimiter;
        }
        sb.append("{");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            sb.append(prefix)
                    .append(entry.getKey()).append("=").append(entry.getValue())
                    .append(pairDelimiter);
        }
        sb.append("}");
    }
}
