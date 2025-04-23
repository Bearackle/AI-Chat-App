package com.workspace.llmsystem.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workspace.llmsystem.service.LlamaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@RestController
public class LlamaController {

    @Autowired
    private LlamaService llamaService;
    private final ObjectMapper objectMapper;
    private final Logger log = LoggerFactory.getLogger(LlamaController.class);
    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080") // địa chỉ llama.cpp server
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public LlamaController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestParam String prompt) {
        Map<String, Object> body = Map.of(
                "prompt", prompt,
                "stream", true
        );
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
                        return sseData;
                    } catch (Exception e){
                        log.error("Loi data bind" +e.getMessage());
                        return "Loi data bind";
                    }
                }).onErrorResume(e -> {
                    // Log lỗi và trả về luồng rỗng hoặc thông báo lỗi
                    log.error("Lỗi khi streaming dữ liệu: {}", e.getMessage());
                    return Flux.just("data: Lỗi server, vui lòng thử lại\n\n");
                })
                .doOnCancel(() -> {
                    // Xử lý khi frontend ngắt kết nối
                    log.info("Frontend đã ngắt kết nối");
                });
        Flux<String> heartbeat = Flux.interval(Duration.ofSeconds(15))
                .map(tick -> ": keep-alive\n\n");
        return Flux.merge(dataFlux, heartbeat)
                .doOnCancel(() -> log.info("Frontend đã ngắt kết nối"))
                .doOnComplete(() -> log.info("Luồng SSE hoàn tất"));
    }
}