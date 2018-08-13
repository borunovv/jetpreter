package com.borunovv.jetpreter.core.util;

import com.borunovv.jetpreter.core.contract.Precondition;

import java.util.List;

/**
 * @author borunovv
 */
public final class CollectionUtils {
    public static String toCommaSeparatedList(List<?> items) {
        return toSeparatedList(items, ",");
    }

    public static String toSeparatedList(List<?> items, String separator) {
        Precondition.notNull(items, "Bad items: null");
        Precondition.notNull(separator, "Bad separator: null");

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
}