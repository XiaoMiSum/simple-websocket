package io.github.xiaomisum.simplewebsocket;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WebSocketServerTest {
    public static void main(String[] args) {
        test();
    }

    public static void test() {
        Server server = new Server(8080);
        WebSocketHandler wsHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                //注册自定义事件监听器
                factory.register(MyEchoSocket.class);
            }
        };
        ContextHandler context = new ContextHandler();
        context.setContextPath("/ws");
        context.setHandler(wsHandler);

        server.setHandler(wsHandler);
        try {
            server.start();
            System.out.println("Server started at: " + server.getURI());
            server.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}