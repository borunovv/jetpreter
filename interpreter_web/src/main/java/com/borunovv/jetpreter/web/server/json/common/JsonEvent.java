package com.borunovv.jetpreter.web.server.json.common;

/**
 * @author borunovv
 */
public abstract class JsonEvent extends JsonMessage {
    public JsonEvent() {
        super("event");
    }
}
