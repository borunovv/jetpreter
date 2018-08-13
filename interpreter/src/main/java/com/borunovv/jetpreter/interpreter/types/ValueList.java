package com.borunovv.jetpreter.interpreter.types;

import java.util.Collections;
import java.util.List;

/**
 * @author borunovv
 */
public class ValueList extends Value {
    private final List<ValueScalar> values;

    public ValueList(List<ValueScalar> values) {
        super(ValueTypes.LIST);
        this.values = values != null ?
                values :
                Collections.EMPTY_LIST;
    }

    public int size() {
        return values.size();
    }

    public ValueScalar get(int index) {
        return values.get(index);
    }

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
