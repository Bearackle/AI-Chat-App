package com.workspace.llmsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RestController
public class LlamaController {

    //@Autowired
//private LlamaService llamaService;
    private final ObjectMapper objectMapper;
    private final Logger log = LoggerFactory.getLogger(LlamaController.class);
    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080") // địa chỉ llama.cpp server
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public LlamaController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

//    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<String> chat(@RequestParam String prompt) {
//
//    }
}