package com.borunovv.jetpreter.web.server.json;

import com.borunovv.jetpreter.web.server.json.common.JsonEvent;

/**
 * @author borunovv
 */
public class UpdateEvent extends JsonEvent{
    private String output;
    private double progress;
    private long session;

    public UpdateEvent(String output, double progress , long session) {
        this.output = output;
        this.progress = progress;
        this.session = session;
    }
}
