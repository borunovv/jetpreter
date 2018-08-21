package com.borunovv.jetpreter.web.server.json.common;

/**
 * @author borunovv
 */
public abstract class JsonRequest extends JsonMessage {
    private String action;

    public JsonRequest(String action) {
        super("request");
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
