package com.borunovv.jetpreter.web.server;

import com.borunovv.jetpreter.web.SessionManager;
import com.borunovv.jetpreter.web.core.wsserver.nio.RWSession;
import com.borunovv.jetpreter.web.server.json.JsonMessageMarshaller;
import com.borunovv.jetpreter.web.server.json.UpdateProgramRequest;
import com.borunovv.jetpreter.web.server.json.common.JsonRequest;
import com.borunovv.jetpreter.web.server.json.common.JsonResponse;

/**
 * @author borunovv
 */
public class MessageHandler implements IStringMessageHandler{

    private SessionManager sessionManager = new SessionManager();

    @Override
    public void start() {
        sessionManager.start();
    }

    @Override
    public void stop() {
        sessionManager.stop();
    }

    @Override
    public String handle(String requestJson, RWSession client) {
        JsonRequest request = JsonMessageMarshaller.unmarshallRequest(requestJson);
        JsonResponse response = handle(request, client);
        return response != null ?
                JsonMessageMarshaller.marshall(response) :
                null;
    }

    private JsonResponse handle(JsonRequest request, RWSession client) {
        if (request instanceof UpdateProgramRequest) {
            return processProgramUpdate((UpdateProgramRequest) request, client);
        } else {
            throw new IllegalArgumentException("Unexpected request: " + request);
        }
    }

    private JsonResponse processProgramUpdate(UpdateProgramRequest request, RWSession client) {
        sessionManager.updateProgram(client, request.getProgram());
        return null;
    }
}
