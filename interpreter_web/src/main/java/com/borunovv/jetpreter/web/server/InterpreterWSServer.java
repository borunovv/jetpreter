package com.borunovv.jetpreter.web.server;

import com.borunovv.jetpreter.web.core.log.Log;
import com.borunovv.jetpreter.web.core.wsserver.nio.IMessageHandler;
import com.borunovv.jetpreter.web.core.wsserver.nio.Server;
import com.borunovv.jetpreter.web.core.wsserver.protocol.websocket.WSMessage;
import com.borunovv.jetpreter.web.core.wsserver.protocol.websocket.client.WSClient;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author borunovv
 */
public class InterpreterWSServer {
    private static final int DEFAULT_PORT = 8888;

    private IStringMessageHandler messageHandler;
    private volatile boolean stopRequested;

    public InterpreterWSServer(IStringMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void execute(String[] args) {
        if (args.length < 1) {
            start(DEFAULT_PORT);
        } else if (args[0].equals("stop")) {
            int port = readPortFromParam(args, 1);
            stop(port);
        } else {
            int port = readPortFromParam(args, 0);
            start(port);
            System.out.println("Usage: program.jar [<port> | stop <port>]");
        }
    }

    private int readPortFromParam(String[] args, int index) {
        return args.length > index ?
                        Integer.parseInt(args[index]) :
                        DEFAULT_PORT;
    }

    public void start(int port) {
        try {

            Server server = new Server(port, 4, new MyHandler());
            server.start();
            messageHandler.start();

            Log.info("Server started on port " + port + ".");
            while (!stopRequested) {
                Thread.sleep(100);
            }
            Log.info("Detected stop request. Stopping..");

            messageHandler.stop();
            server.stop();

        } catch (Exception e) {
            Log.error("Failed to start. ", e);
        }
    }

    public void stop(int port) {
        try (WSClient client  = new WSClient("localhost", port)) {
            client.writeStringMsg("stop_server");
        } catch (IOException e) {
            Log.error("Closing error. It seems server not started (" + e.getMessage() + ")");
        }
    }


    private class MyHandler implements IMessageHandler<WSMessage> {
        private AtomicInteger counter = new AtomicInteger();

        @Override
        public void handle(WSMessage message) {
            int requestIndex = counter.incrementAndGet();

            // Read message from client (we assume client sent us utf8 text,
            // for binary data use message.getBinaryData() and also check message.getType());
            String messageFromClientUtf8 = message.getUtf8Text();

            Log.info("Request #" + requestIndex + " come. "
                    + ", Type: " + message.getType()
                    + ", Content (utf8): '" + messageFromClientUtf8 + "'");

            if (messageFromClientUtf8.equals("stop_server")) {
                stopRequested = true;
                return;
            }

            // Send response to client
            String responseMsg = messageHandler.handle(messageFromClientUtf8, message.getSession());
            if (responseMsg != null) {
                message.getSession().queueMessageToClient(
                        WSMessage.makeUtf8(message.getSession(), responseMsg));
                Log.info("Sent response to client: '" + responseMsg + "'");
            }
        }

        @Override
        public void onReject(WSMessage message) {
            Log.warn("Message rejected: " + message);
        }

        @Override
        public void onError(WSMessage message, Exception cause) {
            Log.error("Error while processing message: " + message, cause);
        }

        @Override
        public void onError(Exception cause) {
            Log.error("Error while processing message", cause);
        }
    }
}
