package com.youfu.aiagent.demo.invoke;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HttpAiInvoke {
    // 从环境变量读取 API Key（避免硬编码）
    private static final String DASHSCOPE_API_KEY = System.getenv("DASHSCOPE_API_KEY");
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    public static void main(String[] args) {
        // 构建请求体
        JSONObject requestBody = buildRequestBody();
        String jsonBody = JSONUtil.toJsonStr(requestBody);

        // 发起 SSE 流式请求
        try (HttpResponse response = HttpRequest.post(API_URL)
                .header("Authorization", "Bearer " + DASHSCOPE_API_KEY)
                .header("Content-Type", "application/json")
                .header("X-DashScope-SSE", "enable")
                // 修正 body 调用方式
                .body(jsonBody)
                .charset(StandardCharsets.UTF_8)
                .timeout(0)
                .execute()) {

            // 校验响应状态
            if (!response.isOk()) {
                System.err.println("请求失败，状态码：" + response.getStatus());
                System.err.println("错误详情：" + response.body());
                return;
            }

            // 逐行读取 SSE 流
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.bodyStream(), StandardCharsets.UTF_8)
            );
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (StrUtil.isBlank(line) || !line.startsWith("data:")) continue;
                String sseData = line.substring(5).trim();
                if (StrUtil.isBlank(sseData)) continue;
                // 解析并打印流式结果
                parseSseData(sseData);
            }
        } catch (Exception e) {
            // 生产环境建议替换为日志框架，这里仅调试用
            System.err.println("请求异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 构建请求体 JSON
     */
    private static JSONObject buildRequestBody() {
        JSONObject root = new JSONObject();
        root.set("model", "deepseek-v4-pro");

        // 对话消息
        JSONObject input = new JSONObject();
        JSONArray messages = new JSONArray();
        JSONObject userMsg = new JSONObject();
        userMsg.set("role", "user");
        userMsg.set("content", "你是谁？");
        messages.add(userMsg);
        input.set("messages", messages);
        root.set("input", input);

        // 模型参数
        JSONObject parameters = new JSONObject();
        parameters.set("enable_thinking", true);
        parameters.set("incremental_output", true);
        parameters.set("result_format", "message");
        root.set("parameters", parameters);

        return root;
    }

    /**
     * 解析 SSE 流式数据
     */
    private static void parseSseData(String jsonStr) {
        JSONObject json = JSONUtil.parseObj(jsonStr);
        String code = json.getStr("code");
        if (!"".equals(code) && !"200".equals(code)) {
            System.err.println("接口异常：" + json.getStr("message"));
            return;
        }

        JSONObject output = json.getJSONObject("output");
        JSONArray choices = output.getJSONArray("choices");
        if (choices.isEmpty()) return;

        JSONObject choice = choices.getJSONObject(0);
        JSONObject message = choice.getJSONObject("message");

        String thinking = message.getStr("thinking", "");
        String content = message.getStr("content", "");

        // 打印思考过程和回答内容
        if (StrUtil.isNotBlank(thinking)) System.out.print(thinking);
        if (StrUtil.isNotBlank(content)) System.out.print(content);
    }
}