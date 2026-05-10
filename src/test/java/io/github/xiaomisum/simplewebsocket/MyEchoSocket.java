package io.github.xiaomisum.simplewebsocket;

import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@WebSocket
public class MyEchoSocket {
    //维持session
    private static final Set<Session> sessions =
            Collections.synchronizedSet(new HashSet<Session>());
    private Session session;

    //建立连接后保存session
    @OnWebSocketOpen
    public void onWebSocketConnect(Session session) {
        this.session = session;
        System.out.println(session.isOpen());
        sessions.add(session);
    }

    //连接关闭，移除session列表
    @OnWebSocketClose
    public void onWebSocketClose(int statusCode, String reason) {
        System.out.println(reason);
        sessions.remove(this.session);
        session.close(statusCode, reason, Callback.NOOP);
    }

    //错误处理
    @OnWebSocketError
    public void onWebSocketError(Throwable cause) {
        System.out.println(cause.getMessage());
    }

    //接收字符串类型消息，并转发给所有客户端
    @OnWebSocketMessage
    public void onWebSocketText(String message) {
        System.out.println("text message:" + message);
        for (final Session session : sessions) {
            session.sendText("server to convert text:" + message, Callback.NOOP);
        }
    }

    //接收二进制消息，并转发给所有客户端
    @OnWebSocketMessage
    public void onWebSocketBinary(ByteBuffer buffer, Callback callback) {
        byte[] payload = new byte[buffer.remaining()];
        buffer.get(payload);
        System.out.println("WebSocketBinary:" + new String(payload));
        buffer.rewind();
        for (final Session session : sessions) {
            session.sendBinary(buffer, Callback.NOOP);
        }
        callback.succeed();
    }
}