# simplehttp

[![License](http://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/XiaoMiSum/simple-websocket/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.xiaomisum/simple-websocket)](https://central.sonatype.com/artifact/io.github.xiaomisum/simple-websocket)
[![MiGoo Author](https://img.shields.io/badge/Author-xiaomi-yellow.svg)](https://github.com/XiaoMiSum)
[![GitHub release](https://img.shields.io/github/release/XiaoMiSum/simple-websocket.svg)](https://github.com/XiaoMiSum/simple-websocket/releases)

## 1. 介绍

一个简单的 websocket client，基于 jetty websocket

## 2. 使用

### Maven 引入

在您的 `pom.xml` 中添加以下依赖：

``` xml
<!-- https://mvnrepository.com/artifact/io.github.xiaomisum/simple-websocket -->
<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>simple-websocket</artifactId>
    <version>${version}</version>
</dependency>
```

### Gradle 引入

在您的 `build.gradle` 中添加：

```gradle
implementation 'io.github.xiaomisum:simple-websocket:${version}'
```

## 3. 使用示例

### 3.1 基本使用

```java 
import io.github.xiaomisum.simplewebsocket.Request;
import io.github.xiaomisum.simplewebsocket.Response;

public class Demo {
    static void main(String[] args) {
        // 创建一个WebSocket请求 Request 
        Request request = new Request("ws://localhost:8080/websocket");
        // 发送文本消息 
        Response response = request.body("Hello WebSocket").execute();
        // 获取响应内容
        System.out.println("Response: " + response.text());
    }
}

```

### 3.2 发送二进制数据

```java 
import io.github.xiaomisum.simplewebsocket.Request;
import io.github.xiaomisum.simplewebsocket.Response;

public class Demo {
    static void main(String[] args) {
        // 创建一个WebSocket请求 Request 
        Request request = new Request("ws://localhost:8080/websocket");
        // 发送二进制数据
        byte[] data = "Hello WebSocket".getBytes(StandardCharsets.UTF_8);
        Response response = request.bytes(data).execute();
        // 获取响应内容
        System.out.println("Response: " + response.text());
    }
}

```

### 3.3 设置请求头

```java 
import io.github.xiaomisum.simplewebsocket.Request;
import io.github.xiaomisum.simplewebsocket.Response;

public class Demo {
    static void main(String[] args) {
        // 创建一个WebSocket请求 Request 
        Request request = new Request("ws://localhost:8080/websocket");
        request.headers(headers -> {
            headers.put("Authorization", "Bearer your-token");
            headers.put("Custom-Header", "custom-value");
        });
        // 发送文本消息 
        Response response = request.body("Hello with headers").execute();
        // 获取响应内容 
        System.out.println("Response: " + response.text());
    }
}

```

### 3.4 设置查询参数

```java 
import io.github.xiaomisum.simplewebsocket.Request;
import io.github.xiaomisum.simplewebsocket.Response;

public class Demo {
    static void main(String[] args) {
        // 创建一个WebSocket请求 Request 
        Request request = new Request("ws://localhost:8080/websocket");
        request.query(query -> {
            query.put("userId", "12345");
            query.put("token", "abcde");
        });
        // 发送文本消息 
        Response response = request.body("Hello with query params").execute();
        // 获取响应内容 
        System.out.println("Response: " + response.text());
    }
}

```

### 3.5 设置超时时间

```java 
import io.github.xiaomisum.simplewebsocket.Request;
import io.github.xiaomisum.simplewebsocket.Response;

public class Demo {
    static void main(String[] args) {
        // 创建一个WebSocket请求 Request 
        Request request = new Request("ws://localhost:8080/websocket");
        // 设置超时时间为30秒（默认60秒）
        request.timeout(30);
        // 发送文本消息 
        Response response = request.body("Hello with query params").execute();
        // 获取响应内容 
        System.out.println("Response: " + response.text());
    }
}

```

### 3.6 使用关闭连接处理函数

```java 
import io.github.xiaomisum.simplewebsocket.Request;
import io.github.xiaomisum.simplewebsocket.Response;

public class Demo {
    static void main(String[] args) {
        // 创建一个WebSocket请求 Request 
        Request request = new Request("ws://localhost:8080/websocket");
        // 发送文本消息 
        Response response = request.body("Hello with query params").execute(close -> close.contains("Hello"));
        // 获取响应内容 
        System.out.println("Response: " + response.text());
    }
}

```

### 3.7 保存响应到文件

```java 
import io.github.xiaomisum.simplewebsocket.Request;
import io.github.xiaomisum.simplewebsocket.Response;

public class Demo {
    static void main(String[] args) {
        Request request = new Request("ws://localhost:8080/websocket");
        Response response = request.body("Hello").execute();
        // 将响应保存到文件 
        String filePath = response.save("/path/to/save/response.txt");
        System.out.println("Response saved to: " + filePath);
    }
}

```