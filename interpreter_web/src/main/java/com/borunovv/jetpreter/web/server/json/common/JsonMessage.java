package com.borunovv.jetpreter.web.server.json.common;

/**
 * @author borunovv
 */
public abstract class JsonMessage {
    private String type;

    public JsonMessage(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
