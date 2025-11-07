/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining
 *  * a copy of this software and associated documentation files (the
 *  * 'Software'), to deal in the Software without restriction, including
 *  * without limitation the rights to use, copy, modify, merge, publish,
 *  * distribute, sublicense, and/or sell copies of the Software, and to
 *  * permit persons to whom the Software is furnished to do so, subject to
 *  * the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be
 *  * included in all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 *  * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package io.github.xiaomisum.simplewebsocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


/**
 * WebSocket服务套接字类，处理WebSocket连接的各个方面
 *
 * @author xiaomi
 * Created at 2025/10/26 21:25
 */
@WebSocket
public class ServiceSocket {

    private final Response response;
    private final Function<String, Boolean> closeConnectHandler;
    private final Function<byte[], String> byteToStringConverter;
    protected WebSocketClient client;
    protected CountDownLatch openLatch = new CountDownLatch(1);
    protected CountDownLatch closeLatch = new CountDownLatch(1);
    protected Session session;
    protected boolean connected = false;

    /**
     * 构造一个新的服务套接字实例
     *
     * @param client                WebSocket客户端
     * @param response              响应对象
     * @param closeConnectHandler   关闭连接处理函数
     * @param byteToStringConverter 字节数组到字符串的转换函数
     */
    public ServiceSocket(WebSocketClient client, Response response, Function<String, Boolean> closeConnectHandler, Function<byte[], String> byteToStringConverter) {
        this.client = client;
        this.response = response;
        this.closeConnectHandler = closeConnectHandler;
        this.byteToStringConverter = byteToStringConverter;
    }

    /**
     * 处理文本消息
     *
     * @param message 接收到的文本消息
     */
    @OnWebSocketMessage
    public void onMessage(String message) {
        if (closeConnectHandler == null || closeConnectHandler.apply(message)) {
            closeLatch.countDown();
        }
    }

    /**
     * 处理二进制消息
     *
     * @param message 接收到的二进制消息
     * @param offset  数据偏移量
     * @param length  数据长度
     */
    @OnWebSocketMessage
    public void onMessage(byte[] message, int offset, int length) {
        // 使用函数式方法将byte[]转换为String
        if (closeConnectHandler == null || closeConnectHandler.apply(byteToStringConverter.apply(message))) {
            closeLatch.countDown();
        }
    }

    /**
     * 处理WebSocket帧
     *
     * @param frame WebSocket帧
     */
    @OnWebSocketFrame
    public void onFrame(Frame frame) {
        response.endTime = System.currentTimeMillis();
        var payload = frame.getPayload();
        byte[] bytes;
        if (payload.hasArray()) {
            bytes = payload.array();
        } else {
            // 对于没有数组支持的ByteBuffer（如直接缓冲区），我们需要复制数据
            bytes = new byte[payload.remaining()];
            payload.duplicate().get(bytes);
        }
        response.bytes = bytes;
    }


    /**
     * 处理WebSocket连接打开事件
     *
     * @param session WebSocket会话
     */
    @OnWebSocketConnect
    public void onOpen(Session session) {
        this.session = session;
        connected = true;
        openLatch.countDown();
    }

    /**
     * 处理WebSocket连接关闭事件
     *
     * @param statusCode 状态码
     * @param reason     关闭原因
     */
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        response.status = statusCode;
        openLatch.countDown();
        closeLatch.countDown();
        connected = false;
        //关闭 WebSocket connection
        try {
            client.stop();
        } catch (Exception ignored) {

        }
    }


    /**
     * 等待连接关闭
     *
     * @param duration 等待时长
     * @param unit     时间单位
     * @return 如果在指定时间内连接关闭则返回true，否则返回false
     * @throws InterruptedException 如果等待过程中线程被中断
     */
    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        boolean res = closeLatch.await(duration, unit);
        close(StatusCode.NORMAL, "closed session.");
        return res;
    }

    /**
     * 等待连接打开
     *
     * @param duration 等待时长
     * @param unit     时间单位
     * @return 如果在指定时间内连接打开则返回true，否则返回false
     * @throws InterruptedException 如果等待过程中线程被中断
     */
    public boolean awaitOpen(int duration, TimeUnit unit) throws InterruptedException {
        return openLatch.await(duration, unit);
    }

    /**
     * 获取WebSocket会话
     *
     * @return WebSocket会话
     */
    public Session getSession() {
        return session;
    }

    /**
     * 发送二进制消息
     *
     * @param message 要发送的二进制消息
     * @throws IOException 如果发送过程中发生IO异常
     */
    public void sendMessage(byte[] message) throws IOException {
        session.getRemote().sendBytes(ByteBuffer.wrap(message));
    }

    /**
     * 发送文本消息
     *
     * @param message 要发送的文本消息
     * @throws IOException 如果发送过程中发生IO异常
     */
    public void sendMessage(String message) throws IOException {
        session.getRemote().sendString(message);
    }

    /**
     * 关闭WebSocket连接
     */
    public void close() {
        close(StatusCode.NORMAL, "closed session.");
    }

    /**
     * 关闭WebSocket连接
     *
     * @param statusCode 状态码
     * @param statusText 状态文本
     */
    public void close(int statusCode, String statusText) {
        //关闭 WebSocket session
        if (session != null) {
            session.close(statusCode, statusText);
        }
    }

    /**
     * 检查WebSocket是否已连接
     *
     * @return 如果已连接返回true，否则返回false
     */
    public boolean isConnected() {
        return connected;
    }
}
