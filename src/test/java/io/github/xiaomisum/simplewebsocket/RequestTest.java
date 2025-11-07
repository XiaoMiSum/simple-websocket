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

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class RequestTest {

    @BeforeClass
    public void beforeClass() {
        Executors.newSingleThreadExecutor().submit(WebSocketServerTest::test);
    }

    @Test
    public void testExecute() throws Exception {
        var start = System.currentTimeMillis();
        var request = new Request("ws://localhost:8080/ws")
                //  .body("hello");
                .bytes("hello".getBytes());
        var response = request.execute();
        System.out.println(response.text());
        System.out.println(response.status());
        System.out.println("cost:" + (System.currentTimeMillis() - start));
    }

    @Test
    public void testExecute2() throws Exception {
        var start = System.currentTimeMillis();
        var request = new Request("ws://localhost:8080/ws")
                //  .body("hello");
                .bytes("你好".getBytes());
        var response = request.execute(x -> Pattern.compile("你").matcher(x).find(), String::new);
        System.out.println(response.text());
        System.out.println("cost:" + (System.currentTimeMillis() - start));
    }

    @Test
    public void testExecute3() throws Exception {
        var start = System.currentTimeMillis();
        var request = new Request("ws://localhost:8080/ws")
                //  .body("hello");
                .bytes("嘻嘻".getBytes());
        var response = request.execute(x -> Pattern.compile("嘻嘻").matcher(x).find());
        System.out.println(response.text());
        System.out.println("cost:" + (System.currentTimeMillis() - start));
    }

    @Test
    public void testExecute4() throws Exception {
        var start = System.currentTimeMillis();
        var request = new Request("ws://localhost:8080/ws").timeout(10)
                //  .body("hello");
                .bytes("哈哈".getBytes());
        var response = request.execute(x -> Pattern.compile("heihei").matcher(x).find(), String::new);
        System.out.println(response.text());
        System.out.println("cost:" + (System.currentTimeMillis() - start));
    }

    @Test
    public void testRequestCreation() {
        String url = "ws://localhost:8080/test";
        Request request = new Request(url);

        Assert.assertEquals(request.url(), url);
        Assert.assertEquals(request.timeout(), 60);
        Assert.assertEquals(request.body(), "");
        Assert.assertEquals(request.bytes().length, 0);
    }

    @Test
    public void testRequestWithBody() {
        String url = "ws://localhost:8080/test";
        String body = "test body";
        Request request = new Request(url).body(body);

        Assert.assertEquals(request.body(), body);
    }

    @Test
    public void testRequestWithBytes() {
        String url = "ws://localhost:8080/test";
        byte[] bytes = "test bytes".getBytes();
        Request request = new Request(url).bytes(bytes);

        Assert.assertEquals(request.bytes(), bytes);
    }

    @Test
    public void testRequestWithQuery() {
        String url = "ws://localhost:8080/test";
        HashMap<String, Object> query = new HashMap<>();
        query.put("param1", "value1");
        query.put("param2", "value2");
        Request request = new Request(url).query(query);

        Assert.assertEquals(request.query(), query.toString());
    }

    @Test
    public void testRequestWithHeaders() {
        String url = "ws://localhost:8080/test";
        Request request = new Request(url);
        request.headers(map -> {
            map.put("Authorization", "Bearer token");
            map.put("Content-Type", "application/json");
        });

        var headers = request.headers();
        Assert.assertEquals(headers.get("Authorization"), "Bearer token");
        Assert.assertEquals(headers.get("Content-Type"), "application/json");
    }

    @Test
    public void testRequestWithCustomTimeout() {
        String url = "ws://localhost:8080/test";
        int timeout = 30;
        Request request = new Request(url).timeout(timeout);

        Assert.assertEquals(request.timeout(), timeout);
    }

    @Test
    public void testRequestWithInvalidTimeout() {
        String url = "ws://localhost:8080/test";
        int timeout = -10; // 无效超时值
        Request request = new Request(url).timeout(timeout);

        // 应该默认为60秒
        Assert.assertEquals(request.timeout(), 60);
    }
}
