package com.workspace.llmsystem.service;

import com.workspace.llmsystem.model.CmsMessage;
import reactor.core.publisher.Flux;

import java.util.List;

public interface CmsMessageService {
    Flux<String> chat(String prompt, Long sid);
    List<CmsMessage> getCompletedChatContent(Long sid);
}
