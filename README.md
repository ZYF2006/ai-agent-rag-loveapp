# LoveApp ❤️

基于 Spring Boot + Spring AI 开发的 AI 恋爱问答应用。

## 项目简介

LoveApp 是一个基于大语言模型的 AI 恋爱问答应用，主要用于学习和实践 Spring AI 在 AI 应用开发中的核心能力。

项目实现了：

- 大语言模型调用
- 多轮对话
- ChatMemory 会话记忆
- Advisor
- RAG 知识库问答
- Embedding 向量化
- VectorStore 向量检索
- 自定义 Advisor 日志记录

## 技术栈

- Java 21
- Spring Boot 3.5.14
- Spring AI 1.0.1
- Spring AI Alibaba
- DashScope
- Maven
- RAG
- VectorStore

## 核心功能

### 1. AI 对话

通过 Spring AI ChatClient 调用大语言模型，实现 AI 恋爱咨询。

### 2. 多轮对话

使用 ChatMemory 保存用户与 AI 的历史对话，实现连续上下文交流。

### 3. RAG 知识库

项目内置恋爱相关 Markdown 知识库。

整体流程：

用户问题
→ Embedding
→ VectorStore 相似度检索
→ 获取相关知识
→ 注入 Prompt
→ 大语言模型生成回答

### 4. 自定义 Advisor

实现 MyLoggerAdvisor，对 AI 请求和响应进行日志记录。

## 项目结构

```text
src
├── main
│   ├── java
│   │   └── com.youfu.aiagent
│   │       ├── advisor
│   │       │   └── MyLoggerAdvisor.java
│   │       ├── app
│   │       │   └── LoveApp.java
│   │       ├── demo
│   │       │   └── invoke
│   │       └── rag
│   │           ├── LoveAppDocumentLoader.java
│   │           └── LoveAppVectorStoreConfig.java
│   └── resources
│       ├── document
│       │   └── 恋爱知识库
│       └── application.yml
└── test
