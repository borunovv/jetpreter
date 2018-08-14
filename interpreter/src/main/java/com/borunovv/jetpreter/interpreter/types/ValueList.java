package com.borunovv.jetpreter.interpreter.types;

import java.util.Collections;
import java.util.List;

/**
 * Value for type 'LIST'.
 * Can hold vector values like [1,2,3,4,5] or [1, 3.1415, 2.71].
 * Used in functions like map/reduce.
 *
 * @author borunovv
 */
public class ValueList extends Value {
    /**
     * Immutable.
     * List of items.
     */
    private final List<ValueScalar> values;

    /**
     * C-tor
     *
     * @param values List of items.
     */
    public ValueList(List<ValueScalar> values) {
        super(ValueTypes.LIST);
        this.values = values != null ?
                values :
                Collections.EMPTY_LIST;
    }

    /**
     * Return list size.
     */
    public int size() {
        return values.size();
    }

    /**
     * Return item at the given index.
     *
     * @param index item index
     */
    public ValueScalar get(int index) {
        return values.get(index);
    }

    /**
     * Return item list string representation like [1, 2, 3, 4, 5].
     * If list is too large (more then 100 items) it will be truncated to 100 items with following '...' at the end.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int maxCount = Math.min(values.size(), 100);

        sb.append("[");
        for (int i = 0; i < maxCount; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(values.get(i).toString());
        }

        boolean isTruncated = maxCount < values.size();
        if (isTruncated) {
            sb.append(", ...");
        }
        sb.append("]");
        return sb.toString();
    }
}
