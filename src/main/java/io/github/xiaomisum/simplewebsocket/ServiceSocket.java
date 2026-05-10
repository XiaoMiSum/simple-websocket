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

import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


/**
 * WebSocket服务套接字类,处理WebSocket连接的各个方面
 *
 * @author xiaomi
 * Created at 2025/10/26 21:25
 */
@WebSocket
public class ServiceSocket {

    private final Response response;
    private final Function<String, Boolean> closeConnectHandler;
    private final Function<byte[], String> byteToStringConverter;
    protected CountDownLatch openLatch = new CountDownLatch(1);
    protected CountDownLatch closeLatch = new CountDownLatch(1);
    protected Session session;
    protected boolean connected = false;

    /**
     * 构造一个新的服务套接字实例
     *
     * @param response              响应对象
     * @param closeConnectHandler   关闭连接处理函数
     * @param byteToStringConverter 字节数组到字符串的转换函数
     */
    public ServiceSocket(Response response, Function<String, Boolean> closeConnectHandler, Function<byte[], String> byteToStringConverter) {
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
        // 保存响应数据
        response.bytes = message.getBytes();
        if (closeConnectHandler == null || closeConnectHandler.apply(message)) {
            closeLatch.countDown();
        }
    }

    /**
     * 处理二进制消息
     *
     * @param buffer   接收到的二进制消息缓冲区
     * @param callback 发送完成回调
     */
    @OnWebSocketMessage
    public void onMessage(ByteBuffer buffer, Callback callback) {
        // 使用函数式方法将byte[]转换为String
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        // 保存响应数据
        response.bytes = data;
        if (closeConnectHandler == null || closeConnectHandler.apply(byteToStringConverter.apply(data))) {
            closeLatch.countDown();
        }
        callback.succeed();
    }

    /**
     * 处理WebSocket连接打开事件
     *
     * @param session WebSocket会话
     */
    @OnWebSocketOpen
    public void onOpen(Session session) {
        this.session = session;
        connected = true;
        openLatch.countDown();
    }

    /**
     * 处理WebSocket连接关闭事件
     *
     * @param statusCode WebSocket会话
     * @param reason 关闭原因
     */
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        response.status = statusCode;
        openLatch.countDown();
        closeLatch.countDown();
        connected = false;
    }

    /**
     * 处理WebSocket错误事件
     *
     * @param throwable 异常
     */
    @OnWebSocketError
    public void onError(Throwable throwable) {
        connected = false;
        openLatch.countDown();
        closeLatch.countDown();
    }


    /**
     * 等待连接关闭
     *
     * @param duration 等待时长
     * @param unit     时间单位
     * @return 如果在指定时间内连接关闭则返回true,否则返回false
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
     * @return 如果在指定时间内连接打开则返回true,否则返回false
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
        session.sendBinary(ByteBuffer.wrap(message), Callback.NOOP);
    }

    /**
     * 发送文本消息
     *
     * @param message 要发送的文本消息
     * @throws IOException 如果发送过程中发生IO异常
     */
    public void sendMessage(String message) throws IOException {
        session.sendText(message, Callback.NOOP);
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
            session.close(statusCode, statusText, Callback.NOOP);
        }
    }

    /**
     * 检查WebSocket是否已连接
     *
     * @return 如果已连接返回true,否则返回false
     */
    public boolean isConnected() {
        return connected;
    }
}
