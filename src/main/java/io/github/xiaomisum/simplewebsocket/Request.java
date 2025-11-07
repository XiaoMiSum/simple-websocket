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


import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.HttpClientProvider;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.common.scopes.SimpleContainerScope;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * WebSocket请求类，用于构建和执行WebSocket请求
 *
 * @author xiaomi
 * Created at 2025/10/26 21:25
 */
public class Request {
    private String body;
    private byte[] bytes;

    private String url;

    private int timeout = 60;

    private Map<String, String> headers;

    private Map<String, Object> query;


    /**
     * 构造一个新的WebSocket请求
     *
     * @param url WebSocket服务地址
     */
    public Request(String url) {
        this.url = url;
    }

    /**
     * 设置请求体文本内容
     *
     * @param body 请求体文本内容
     * @return 当前请求实例
     */
    public Request body(String body) {
        this.body = body;
        return this;
    }

    /**
     * 设置请求体字节内容
     *
     * @param bytes 请求体字节内容
     * @return 当前请求实例
     */
    public Request bytes(byte[] bytes) {
        this.bytes = bytes;
        return this;
    }

    /**
     * 设置查询参数
     *
     * @param customizer 查询参数自定义器
     * @return 当前请求实例
     */
    public Request query(Customizer<Map<String, Object>> customizer) {
        var query = new HashMap<String, Object>();
        customizer.customize(query);
        this.query = query;
        return this;
    }

    /**
     * 设置查询参数
     *
     * @param query 查询参数映射
     * @return 当前请求实例
     */
    public Request query(Map<String, Object> query) {
        this.query = query;
        return this;
    }

    /**
     * 设置请求头
     *
     * @param customizer 请求头自定义器
     * @return 当前请求实例
     */
    public Request headers(Customizer<Map<String, String>> customizer) {
        var headers = new HashMap<String, String>();
        customizer.customize(headers);
        this.headers = headers;
        return this;
    }

    /**
     * 设置请求头
     *
     * @param headers 请求头
     * @return 当前请求实例
     */
    public Request headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * 设置请求超时时间
     *
     * @param timeout 超时时间（秒）
     * @return 当前请求实例
     */
    public Request timeout(int timeout) {
        this.timeout = timeout > 0 ? timeout : 60;
        return this;
    }

    /**
     * 执行WebSocket请求
     *
     * @return 响应对象
     * @throws Exception 执行过程中可能抛出的异常
     */
    public Response execute() throws Exception {
        return execute(null);
    }

    /**
     * 执行WebSocket请求
     *
     * @param closeConnectHandler 关闭连接处理函数
     * @return 响应对象
     * @throws Exception 执行过程中可能抛出的异常
     */
    public Response execute(Function<String, Boolean> closeConnectHandler) throws Exception {
        return execute(closeConnectHandler, null);
    }

    /**
     * 执行WebSocket请求
     *
     * @param closeConnectHandler   关闭连接处理函数
     * @param byteToStringConverter 字节数组到字符串的转换函数
     * @return 响应对象
     * @throws Exception 执行过程中可能抛出的异常
     */
    public Response execute(Function<String, Boolean> closeConnectHandler, Function<byte[], String> byteToStringConverter) throws Exception {
        var response = new Response(System.currentTimeMillis());
        var client = new WebSocketClient(HttpClientProvider.get(new SimpleContainerScope(WebSocketPolicy.newClientPolicy())));
        var socket = new ServiceSocket(client, response, closeConnectHandler, byteToStringConverter == null ? String::new : byteToStringConverter);
        var request = new ClientUpgradeRequest();
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(request::setHeader);
        }
        url = url + getQueryString();
        client.start();
        client.connect(socket, new URI(url), request);
        socket.awaitOpen(timeout, TimeUnit.SECONDS);

        if (bytes != null && bytes.length > 0) {
            socket.sendMessage(bytes);
        } else if (body != null) {
            socket.sendMessage(body);
        }
        socket.awaitClose(timeout, TimeUnit.SECONDS);
        return response;
    }

    /**
     * 构建查询字符串
     *
     * @return 查询字符串
     */
    private String getQueryString() {
        var queryString = new StringJoiner("&");
        if (query != null && !query.isEmpty()) {
            query.forEach((key, value) -> queryString.add(key + "=" + value));
        }
        return queryString.length() > 0 ? "?" + queryString : queryString.toString();
    }


    /**
     * 获取请求体文本内容
     *
     * @return 请求体文本内容
     */
    public String body() {
        return body == null ? "" : body;
    }

    /**
     * 获取请求体字节内容
     *
     * @return 请求体字节内容
     */
    public byte[] bytes() {
        return bytes == null ? new byte[]{} : bytes;
    }

    /**
     * 获取查询参数字符串表示
     *
     * @return 查询参数字符串
     */
    public String query() {
        return query == null ? "" : query.toString();
    }

    /**
     * 获取请求头映射
     *
     * @return 请求头映射
     */
    public Map<String, String> headers() {
        return headers == null ? new HashMap<>() : headers;
    }

    /**
     * 获取WebSocket服务地址
     *
     * @return WebSocket服务地址
     */
    public String url() {
        return url;
    }

    /**
     * 获取超时时间
     *
     * @return 超时时间（秒）
     */
    public int timeout() {
        return timeout;
    }

}
