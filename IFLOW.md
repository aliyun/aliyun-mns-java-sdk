# Aliyun MNS Java SDK 项目上下文

## 项目概述

本项目是阿里云消息服务（MNS）的 Java SDK，用于与阿里云消息服务进行交互。它提供了创建、管理队列（Queue）和主题（Topic）以及发送、接收、删除消息等功能。

主要技术栈：
- Java 8
- Maven 构建工具
- Apache HttpClient (httpasyncclient)
- JAXB (用于 XML 序列化/反序列化)
- SLF4J (日志接口)
- Google Gson (JSON 处理)
- Apache Commons Lang3

## 目录结构

- `src/main/java/com/aliyun/mns`: SDK 主要源代码
- `src/main/resources`: 配置文件和资源
- `src/test/java/com/aliyun/mns/sample`: 用户使用样例
- `src/test/java/com/aliyun/mns/unitTest`: 单元测试
- `src/test/java/com/aliyun/mns/issueTest`: 特定问题测试

## 核心组件

### 客户端 (Client)
- `MNSClient`: 定义了与 MNS 服务交互的核心接口，包括创建/获取队列和主题、设置账户属性等。
- `DefaultMNSClient`: `MNSClient` 的默认实现。
- `CloudQueue`: 表示一个队列对象，提供队列的所有操作，如发送消息、接收消息、删除消息、设置队列属性等。
- `CloudTopic`: 表示一个主题对象，提供主题的所有操作，如发布消息、订阅/取消订阅、设置主题属性等。

### 模型 (Model)
- `Message`: 表示队列中的消息，包含消息体、优先级、延迟时间、句柄等属性。
- `QueueMeta`: 表示队列的元数据，如队列名称、最大消息大小、消息保留周期等。
- `TopicMeta`: 表示主题的元数据，如主题名称、最大消息大小、消息保留周期等。
- `SubscriptionMeta`: 表示订阅的元数据，如订阅名称、通知端点等。

## 构建和运行

### 环境要求
- Java 8 或更高版本
- Maven 3.x 或更高版本

### 构建项目
```bash
mvn clean package
```
该命令将编译源代码、运行测试并打包生成 JAR 文件。

### 运行测试
```bash
mvn test
```
该命令将运行所有单元测试。

### 安装到本地仓库
```bash
mvn clean install
```

## 使用样例

1. 配置阿里云 AccessKey (AK/SK) 环境变量。
2. 在 `{user.home}/.aliyun-mns.properties` 文件中配置基本值，例如：
   ```properties
   mns.region=cn-hangzhou
   mns.accountendpoint=http://123.mns.cn-hangzhou.aliyuncs.com
   ```
3. 参考 `src/test/java/com/aliyun/mns/sample` 目录下的样例代码。

## 开发约定

- 遵循 Java 编码规范。
- 使用 SLF4J 进行日志记录。
- 使用 JUnit 和 Mockito 进行单元测试。
- 使用 JAXB 进行 XML 序列化/反序列化。
- 所有对外暴露的接口和类都应有清晰的 JavaDoc 注释。