package com.borunovv.jetpreter.web;

import com.borunovv.jetpreter.core.threads.WithOwnThread;
import com.borunovv.jetpreter.interpreter.InterpreterServer;
import com.borunovv.jetpreter.web.core.log.Log;
import com.borunovv.jetpreter.web.core.util.JsonUtils;
import com.borunovv.jetpreter.web.core.wsserver.nio.RWSession;
import com.borunovv.jetpreter.web.core.wsserver.protocol.websocket.WSMessage;
import com.borunovv.jetpreter.web.server.json.UpdateEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * @author borunovv
 */
public class SessionManager extends WithOwnThread {

    private ConcurrentHashMap<RWSession, SessionInfo> sessions = new ConcurrentHashMap<>();

    public void start() {
        super.start();
    }

    public void stop() {
        super.stop();
    }

    public void updateProgram(RWSession client, String program) {
        SessionInfo info = sessions.get(client);
        if (info == null) {
            info = new SessionInfo();
            SessionInfo prev = sessions.putIfAbsent(client, info);
            if (prev != null) {
                info = prev;
            } else {
                info.start();
            }
        }
        info.updateProgram(program);
    }

    @Override
    protected void onThreadStart() {

    }

    @Override
    protected void onThreadStop() {
        for (Map.Entry<RWSession, SessionInfo> entry : sessions.entrySet()) {
            entry.getValue().stop();
        }
        sessions.clear();
    }

    @Override
    protected void onThreadError(Throwable e) {
        Log.error("SessionManager error in worker thread.", e);
    }

    @Override
    protected void onThreadIteration() {
        try {
            for (Map.Entry<RWSession, SessionInfo> entry : sessions.entrySet()) {
                if (isStopRequested()) break;
                RWSession client = entry.getKey();
                SessionInfo info = entry.getValue();
                if (client.isClosed()) {
                    sessions.remove(client);
                    info.stop();
                    continue;
                }

                String out = info.extractOutput();
                double progress = info.getProgress();
                long sessId = info.getSessionId();

                if (info.lastSentProgress != progress || info.lastSentSessionId != sessId || out != null) {
                    info.lastSentSessionId = sessId;
                    info.lastSentProgress = progress;
                    sendEvent(client, out, progress, sessId);
                }
            }
            sleep(1000);
        } catch (Exception e) {
            Log.error("Error in worker thread.", e);
        }
    }

    private void sendEvent(RWSession client, String out, double progress, long sessionId) {
        client.queueMessageToClient(WSMessage.makeUtf8(client, JsonUtils.toJson(
                new UpdateEvent(out, progress, sessionId))));
    }

    private class SessionInfo {
        private final InterpreterServer server = new InterpreterServer();
        private InterpreterServer.IProgramTask serverTask;
        private final AtomicLong sessionId = new AtomicLong();
        private StringBuilder output = new StringBuilder();
        private final Object outputLock = new Object();
        double lastSentProgress;
        long lastSentSessionId;

        SessionInfo() {
        }

        void start() {
            server.start();
        }

        void stop() {
            server.stop();
        }

        void updateProgram(String program) {
            sessionId.incrementAndGet();
            extractOutput();
            Consumer<String> stdout = new SessionAwareConsumer(sessionId, this::writeToOutput);
            Consumer<String> stderr = new SessionAwareConsumer(sessionId, this::writeToErr);
            this.serverTask = server.submitProgram(program, stdout, stderr);
        }

        double getProgress() {
            return serverTask != null ?
                    serverTask.getProgress() :
                    0;
        }

        void writeToOutput(String str) {
            synchronized (outputLock) {
                output.append(str);
            }
        }

        void writeToErr(String str) {
            synchronized (outputLock) {
                output.append("\n").append(str);
            }
        }

        String extractOutput() {
            synchronized (outputLock) {
                if (output.length() > 0) {
                    String result = output.toString();
                    output = new StringBuilder();
                    return result;
                }
            }
            return null;
        }

        long getSessionId() {
            return sessionId.get();
        }
    }

    private static class SessionAwareConsumer implements Consumer<String> {
        private final long sessionId;
        final Consumer<String> proxi;
        final AtomicLong currentSessionId;

        private SessionAwareConsumer(AtomicLong currentSessionId, Consumer<String> proxi) {
            this.sessionId = currentSessionId.get();
            this.proxi = proxi;
            this.currentSessionId = currentSessionId;
        }

        @Override
        public void accept(String s) {
            if (sessionId == currentSessionId.get()) {
                proxi.accept(s);
            }
        }
    }
}
