package com.borunovv.jetpreter.web.server.json;

import com.borunovv.jetpreter.web.server.json.common.JsonRequest;

/**
 * @author borunovv
 */
public class UpdateProgramRequest extends JsonRequest {
    private String program;

    public UpdateProgramRequest(){ super("update_program");
    }

    public String getProgram() {
        return program;
    }
}
