package io.github.xiaomisum.simplewebsocket;

import org.eclipse.jetty.ee10.websocket.server.JettyServerUpgradeRequest;
import org.eclipse.jetty.ee10.websocket.server.JettyServerUpgradeResponse;
import org.eclipse.jetty.ee10.websocket.server.JettyWebSocketCreator;
import org.eclipse.jetty.ee10.websocket.server.config.JettyWebSocketServletContainerInitializer;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class WebSocketServerTest {
    public static void main(String[] args) {
        test();
    }

    public static void test() {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");
        server.setHandler(contextHandler);

        // 配置 WebSocket
        JettyWebSocketServletContainerInitializer.configure(contextHandler, (context, configurator) -> {
            configurator.addMapping("/ws/*", new JettyWebSocketCreator() {
                @Override
                public Object createWebSocket(JettyServerUpgradeRequest request, JettyServerUpgradeResponse response) {
                    return new MyEchoSocket();
                }
            });
        });

        try {
            server.start();
            System.out.println("Server started at: " + server.getURI());
            // 测试场景：不调用 join()，让服务器在后台运行
            // server.join();  // 仅 standalone 模式使用
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}