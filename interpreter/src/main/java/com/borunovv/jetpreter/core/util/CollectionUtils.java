package com.borunovv.jetpreter.core.util;

import com.borunovv.jetpreter.core.contract.Precondition;

import java.util.List;

/**
 * Some collection helpers.
 *
 * @author borunovv
 */
public final class CollectionUtils {
    /**
     * Return comma-separated list of items like "1,2,3".
     *
     * @param items items list
     * @return comma-separated list of items like "1,2,3".
     */
    public static String toCommaSeparatedList(List<?> items) {
        return toSeparatedList(items, ",");
    }


    /**
     * Return separated list of items using given separator.
     *
     * @param separator separator string
     * @return separated list of items using given separator.
     */
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