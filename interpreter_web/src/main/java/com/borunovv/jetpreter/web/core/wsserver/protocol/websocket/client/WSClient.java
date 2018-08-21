package com.borunovv.jetpreter.web.core.wsserver.protocol.websocket.client;

import com.borunovv.jetpreter.web.core.log.Log;
import com.borunovv.jetpreter.web.core.util.StringUtils;
import com.borunovv.jetpreter.web.core.wsserver.protocol.websocket.WSMessage;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

/**
 * @author borunovv
 */
public class WSClient implements Closeable {
    private Socket socket;
    private byte[] buffer = new byte[1024 * 10];
    private String host;
    private int port;
    private MsgBuilder msgBuilder = new MsgBuilder();

    public WSClient(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.socket = new Socket(host, port);
        doHandShake();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    public WSMessage readMsg() throws IOException {
        WSMessage msg = msgBuilder.tryGetNext();
        while (msg == null) {
            msgBuilder.append(read());
            msg = msgBuilder.tryGetNext();
        }
        return msg;
    }

    public String readStringMsg() throws IOException {
        WSMessage msg = readMsg();
        if (msg.getType() != WSMessage.Type.UTF8) {
            throw new RuntimeException("Expected message type UTF8. Actual: " + msg.getType() + ".\n" + msg);
        }
        return msg.getUtf8Text();
    }

    public void writeMsg(WSMessage msg) throws IOException {
        write(msg.marshall());
    }

    public void writeStringMsg(String msg) throws IOException {
        writeMsg(WSMessage.makeUtf8(null, msg));
    }

    private String doHandShake() throws IOException {
        String handshake = makeHandshake(host, port, "example.com");
        byte[] data = StringUtils.uft8StringToBytes(handshake);

        Log.trace("WSClient: -> Send:\n" + handshake);
        write(data);

        byte[] responseData = read();
        String response = StringUtils.toUtf8String(responseData);
        Log.trace("WSClient: <- Receive:\n" + response);

        return response;
    }

    private void write(byte[] data) throws IOException {
        socket.getOutputStream().write(data);
    }

    private byte[] read() throws IOException {
        int red = socket.getInputStream().read(buffer);
        if (red >= 0) {
            return Arrays.copyOfRange(buffer, 0, red);
        }
        return null;
    }

    private String makeHandshake(String host, int port, String originHost) {
        String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        String keyBase64 = "6oT4v/eAbo7hqEoUHLpNgA==";

        String request = ""
                + "GET ws://" + host + ":" + port + "/ HTTP/1.1\r\n" +
                "Host: " + host + ":" + port + "\r\n" +
                "Connection: Upgrade\r\n" +
                "Pragma: no-cache\r\n" +
                "Cache-Control: no-cache\r\n" +
                "Upgrade: websocket\r\n" +
                "Origin: " + originHost + "\r\n" +
                "Sec-WebSocket-Version: 13\r\n" +
                "User-Agent: The best agent ever\r\n" +
                "Accept-Encoding: gzip, deflate\r\n" +
                "Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4\r\n" +
                "Sec-WebSocket-Key: " + keyBase64 + "\r\n" +
                "Sec-WebSocket-Extensions: permessage-deflate; client_max_window_bits\r\n" +
                "\r\n";

        return request;
    }

    private class MsgBuilder {
        private byte[] data = new byte[1024 * 10];
        private int size;

        void append(byte[] buff) {
            if (buff.length == 0) return;
            int elapsed = data.length - size;
            if (elapsed >= buff.length) {
                System.arraycopy(data, size, buff, 0, buff.length);
            } else {
                byte[] temp = new byte[(size + buff.length) * 2];
                System.arraycopy(data, 0, temp, 0, size);
                System.arraycopy(buff, 0, temp, size, buff.length);
                data = temp;
            }
            size += buff.length;
        }

        WSMessage tryGetNext() {
            if (size > 0) {
                int packetSize = WSMessage.tryParse(data, size);
                if (packetSize > 0) {
                    WSMessage msg = new WSMessage(null, data, packetSize);
                    System.arraycopy(data, packetSize, data, 0, size - packetSize);
                    size -= packetSize;
                    return msg;
                }
            }
            return null;
        }
    }
}
