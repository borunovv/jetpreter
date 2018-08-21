package com.borunovv.jetpreter.web.server.json;

import com.borunovv.jetpreter.web.server.json.common.JsonResponse;

/**
 * @author borunovv
 */
public class UpdateProgramResponse extends JsonResponse {
    private long session;
    public UpdateProgramResponse(long session) {
        this.session = session;
    }
}
