# mns java sdk

## 一、基础介绍
Aliyun MNS  JAVA SDK Documents: [https://help.aliyun.com/zh/mns/developer-reference/release-notes-of-the-sdk-for-java](https://help.aliyun.com/zh/mns/developer-reference/release-notes-of-the-sdk-for-java)

Aliyun MNS Console: [https://mns.console.aliyun.com/](https://mns.console.aliyun.com/)

## 二、目录结构

1. sdk 主目录：src/main/java/com/aliyun/mns
2. 用户 sample 样例目录： src/test/java/com/aliyun/mns/sample
3. sdk 单测目录：src/test/java/com/aliyun/mns/unitTest
3. sdk 特定问题目录：src/test/java/com/aliyun/mns/issueTest

## 二、Samples

### 2.1 前置准备

1. 遵循阿里云规范，env 设置 ak、sk
> 详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
2. {"user.home"}/.aliyun-mns.properties 文件配置 基本值，样例：
```properties
mns.region=cn-hangzhou
mns.accountendpoint=http://123.mns.cn-hangzhou.aliyuncs.com
```