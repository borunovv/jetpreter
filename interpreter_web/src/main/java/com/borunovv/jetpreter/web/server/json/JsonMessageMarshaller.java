package com.borunovv.jetpreter.web.server.json;

import com.borunovv.jetpreter.web.core.util.JsonUtils;
import com.borunovv.jetpreter.web.server.json.common.JsonMessage;
import com.borunovv.jetpreter.web.server.json.common.JsonRequest;

import java.util.Map;

/**
 * @author borunovv
 */
public class JsonMessageMarshaller {

    @SuppressWarnings("unchecked")
    public static JsonRequest unmarshallRequest(String json) {
        Map<String, Object> params = (Map<String, Object>) JsonUtils.fromJson(json, Map.class);
        String type = (String) params.get("type");
        switch (type) {
            case "request": {
                String action = (String) params.get("action");
                switch (action) {
                    case "update_program" :
                        return JsonUtils.fromJson(json, UpdateProgramRequest.class);
                    default:
                        throw new IllegalArgumentException("Undefined action '" + action + "'.\nJson: " + json);
                }
            }

            default:
                throw new IllegalArgumentException("Undefined type '" + type + "'.\nJson: " + json);
        }
    }

    public static String marshall(JsonMessage msg) {
        return JsonUtils.toJson(msg);
    }
}
