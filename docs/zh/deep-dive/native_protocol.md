ClickHouse 客户端-服务端原生通信协议
===

## 建立连接

```mermaid
sequenceDiagram
客户端 -> 服务端: 开启 socket 连接
服务端 --> 客户端: ok，新的客户端
Note right of 服务端: 连接建立成功
客户端 -> 服务端: 发送 Hello 请求
服务端 --> 客户端: Hello 返回
Note left of 客户端: 获取到了服务相关信息
```

## 发请请求

- 请求有非常多种不同的 requests/response, 上述 `hello` 为其中一种。

- 可以在 `com.github.housepower.jdbc.protocol` 包下面找到所有的 requests/response 类型。

## 查询

当连接建立并且经过 hello请求返回后，我们可以发送一个字符串SQL来查询数据。

```mermaid
sequenceDiagram
客户端 -> 服务端: 发送 DataRequest 请求
Note right of 服务端: 新的请求来啦，<br/> 我将进行处理
服务端 --> 客户端: 返回 DataResponse
Note left of 客户端: 我拿到了 DataResponse 了
Note left of 客户端: 我将解析 DataResponse 成 ResultSet
```

## 插入

一些小的查询请求，可以以字符串SQL的方式和服务端交互，但这不利于批量数据的插入。ClickHouse 提供了另外原生的批量导入协议支持，这样我们可以直接往ClickHouse发送block数据。


```mermaid
sequenceDiagram
客户端 -> 服务端: 发送Insert请求到服务端 (也称之 PreparedStatement)
Note right of 服务端: 新的PreparedStatement请求来了,<br/>我将看看对应的表的元数据信息
服务端 --> 客户端: 返回 DataResponse (空的 Block, <br/> 也称之 sampleBlock)
Note right of 服务端: State: 等待插入。
Note left of 客户端: 拿到 sampleBlock 了, <br/> 我知道了这个表的元数据（字段名称，类型等）
Note left of 客户端: 写入数据到内存的 block中 <br/> (当我们在 JDBC 调用 `setObject` 的时候)
客户端 -> 服务端: 将 block 包装成 dataRequest 发送
Note right of 服务端: 新的 block 来了,  <br/> 我将插入到表中
客户端 -> 服务端: 发送一个空的 block 结束 插入
Note right of 服务端: 空的 block 来了,  <br/> 请求结束
Note right of 服务端: State: 空闲
```

 

