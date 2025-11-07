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
import org.testng.annotations.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseTest {

    @Test
    public void testResponseCreation() {
        long startTime = System.currentTimeMillis();
        Response response = new Response(startTime);

        Assert.assertEquals(response.startTime().longValue(), startTime);
        Assert.assertNull(response.endTime());
        Assert.assertEquals(response.status(), 1000);
        Assert.assertNull(response.bytes());
    }

    @Test
    public void testResponseText() {
        long startTime = System.currentTimeMillis();
        Response response = new Response(startTime);

        String text = "Hello, World!";
        response.bytes = text.getBytes(StandardCharsets.UTF_8);

        Assert.assertEquals(response.text(), text);
    }

    @Test
    public void testResponseTextWithConverter() {
        long startTime = System.currentTimeMillis();
        Response response = new Response(startTime);

        String text = "Hello, World!";
        response.bytes = text.getBytes(StandardCharsets.UTF_8);

        String result = response.text(String::new);
        Assert.assertEquals(result, text);
    }

    @Test
    public void testResponseSave() throws Exception {
        long startTime = System.currentTimeMillis();
        Response response = new Response(startTime);

        String text = "Hello, World!";
        response.bytes = text.getBytes(StandardCharsets.UTF_8);

        String tempFilePath = System.getProperty("java.io.tmpdir") + File.pathSeparator + "test_response.txt";
        String resultPath = response.save(tempFilePath);

        // 验证文件已创建并包含正确内容
        Assert.assertEquals(resultPath, tempFilePath);
        Path path = Path.of(tempFilePath);
        Assert.assertTrue(Files.exists(path));
        Assert.assertEquals(Files.readString(path), text);

        // 清理临时文件
        Files.deleteIfExists(path);
    }

    @Test
    public void testResponseEndTime() {
        long startTime = System.currentTimeMillis();
        Response response = new Response(startTime);

        long endTime = System.currentTimeMillis() + 1000; // 模拟1秒后结束
        response.endTime = endTime;

        Assert.assertEquals(response.endTime().longValue(), endTime);
    }

    @Test
    public void testResponseStatus() {
        long startTime = System.currentTimeMillis();
        Response response = new Response(startTime);

        int status = 200;
        response.status = status;

        Assert.assertEquals(response.status(), status);
    }
}
