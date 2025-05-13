package com.workspace.llmsystem.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workspace.llmsystem.dao.CmsMessageDao;
import com.workspace.llmsystem.mapper.CmsMessageMapper;
import com.workspace.llmsystem.model.CmsMessage;
import com.workspace.llmsystem.model.CmsMessageExample;
import com.workspace.llmsystem.model.UmsUser;
import com.workspace.llmsystem.service.CmsMessageService;
import com.workspace.llmsystem.service.UmsUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CmsMessageServiceImpl implements CmsMessageService {
    private String baseUrl;
    private final ObjectMapper objectMapper;
    private final Logger log = LoggerFactory.getLogger(CmsMessageServiceImpl.class);

    private final WebClient webClient;
    @Autowired
    private CmsMessageMapper cmsMessageMapper;
    @Autowired
    private UmsUserService umsUserService;
    @Autowired
    private CmsMessageDao cmsMessageDao;

    public CmsMessageServiceImpl(ObjectMapper objectMapper,@Value("${llama.base-url}") String baseUrl) {
        this.objectMapper = objectMapper;
        log.info("baseurl:{}", baseUrl);
        this.baseUrl = baseUrl;
        webClient =  WebClient.builder()
                .baseUrl(baseUrl) // địa chỉ llama.cpp server
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    @Override
    public Flux<String> chat(String prompt,Long sid) {
        UmsUser umsUser = umsUserService.getCurrentUser();
        CmsMessage cmsMessage = new CmsMessage();
        cmsMessage.setSessionId(sid);
        cmsMessage.setUserId(umsUser.getId());
        cmsMessage.setContent(prompt);
        cmsMessage.setIsUser(1);
        cmsMessage.setCreatedAt(new Date());
        cmsMessageMapper.insertSelective(cmsMessage);
        Map<String, Object> body = Map.of(
                "prompt",  prompt,
                "stream", true
        );
        log.info("url to call : {}", webClient.get());
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
                    log.error("failed occur when streaming data: {}", e.getMessage());
                    return Flux.just("data: Failed to connect server\n\n");
                })
                .doOnCancel(() -> {
                    log.info("Frontend disconnected");
                })
                .doOnComplete(() -> {
                    String messageContent = chatAccumulator.toString();
                    CmsMessage messageFromModel = new CmsMessage();
                    BeanUtils.copyProperties(cmsMessage, messageFromModel);
                    messageFromModel.setContent(messageContent);
                    messageFromModel.setCreatedAt(new Date());
                    messageFromModel.setIsUser(0);
                    cmsMessageMapper.insertSelective(messageFromModel);
                });
        Flux<String> heartbeat = Flux.interval(Duration.ofSeconds(15))
                .map(tick -> ": keep-alive\n\n");
        return Flux.merge(dataFlux, heartbeat)
                .doOnCancel(() -> log.info("Frontend disconnected Cancel"))
                .doOnComplete(() -> log.info("SSE streaming completed"));
    }
    @Override
    public List<CmsMessage> getCompletedChatContent(Long sid) {
        List<CmsMessage> ms = cmsMessageDao.getFullContentBySession(sid);
        log.info("getCompletedChatContent:{}", ms.get(0).getContent());
        return ms;
    }
}
