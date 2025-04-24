package com.workspace.llmsystem.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workspace.llmsystem.model.CmsMessage;
import com.workspace.llmsystem.service.CmsMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class CmsMessageServiceImpl implements CmsMessageService {
    @Value("${llama.base-url}")
    private String baseUrl;
    private final ObjectMapper objectMapper;

    private final Logger log = LoggerFactory.getLogger(CmsMessageServiceImpl.class);

    private final WebClient webClient;


    public CmsMessageServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        webClient =  WebClient.builder()
                .baseUrl("http://localhost:8080") // địa chỉ llama.cpp server
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    @Override
    public Flux<String> chat(String prompt) {
        Map<String, Object> body = Map.of(
                "prompt", prompt,
                "stream", true
        );
        StringBuilder chatAccumulator = new StringBuilder();
        Flux<String> dataFlux =  webClient.post()
                .uri("/completion")
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(String.class)
                .map(chunk -> {
                    try {
                        JsonNode json = objectMapper.readTree(chunk);
                        String content = json.get("content").asText();
                        boolean stop = json.get("stop").asBoolean();
                        String sseData = "data: " + content.replace("\n", "\\n") + "\n\n";
                        if (stop) {
                            sseData += "data: [DONE]\n\n";
                        }
                        chatAccumulator.append(content).append(" ");
                        return sseData;
                    } catch (Exception e){
                        log.error("Data bind error" +e.getMessage());
                        return "Data bind error";
                    }
                }).onErrorResume(e -> {
                    // Log lỗi và trả về luồng rỗng hoặc thông báo lỗi
                    log.error("failed occur when streaming data: {}", e.getMessage());
                    return Flux.just("data: Failed to connect server\n\n");
                })
                .doOnCancel(() -> {
                    // Xử lý khi frontend ngắt kết nối
                    log.info("Frontend disconnected");
                })
                .doOnComplete(() -> {
                    String messageContent = chatAccumulator.toString();
                });
        Flux<String> heartbeat = Flux.interval(Duration.ofSeconds(15))
                .map(tick -> ": keep-alive\n\n");
        return Flux.merge(dataFlux, heartbeat)
                .doOnCancel(() -> log.info("Frontend disconnected"))
                .doOnComplete(() -> log.info("SSE streaming completed"));
    }
    @Override
    public List<CmsMessage> getCompletedChatContent(Long sid) {
        return null;
    }
}
