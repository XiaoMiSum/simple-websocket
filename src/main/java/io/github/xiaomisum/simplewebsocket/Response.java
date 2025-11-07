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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * WebSocket响应类，封装了WebSocket通信的响应信息
 *
 * @author xiaomi
 * Created at 2025/10/26 21:25
 */
public class Response {

    /**
     * 请求开始时间戳
     */
    private final Long startTime;

    /**
     * 请求结束时间戳
     */
    Long endTime;
    int status = 1000;

    byte[] bytes;


    /**
     * 构造一个新的响应对象
     *
     * @param startTime 请求开始时间戳
     */
    public Response(long startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取请求开始时间戳
     *
     * @return 请求开始时间戳
     */
    public Long startTime() {
        return startTime;
    }

    /**
     * 获取请求结束时间戳
     *
     * @return 请求结束时间戳
     */
    public Long endTime() {
        return endTime;
    }

    /**
     * 获取响应状态码
     *
     * @return 响应状态码
     */
    public int status() {
        return status;
    }

    /**
     * 获取响应字节数据
     *
     * @return 响应字节数据
     */
    public byte[] bytes() {
        return bytes;
    }

    /**
     * 获取响应体的文本表示
     *
     * @return 响应体文本
     */
    public String text() {
        return text(byteToStringConverter -> new String(bytes));
    }

    /**
     * 使用指定的转换器获取响应体的文本表示
     *
     * @param byteToStringConverter 文本转换器
     * @return 响应体文本
     */
    public String text(Function<byte[], String> byteToStringConverter) {
        return byteToStringConverter.apply(bytes);
    }

    /**
     * 将响应体保存到指定路径的文件中
     *
     * @param path 文件路径
     * @return 文件路径
     */
    public String save(String path) {
        return text(byteToStringConverter -> {
            try {
                return Files.write(Path.of(path), bytes, CREATE, TRUNCATE_EXISTING).toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
