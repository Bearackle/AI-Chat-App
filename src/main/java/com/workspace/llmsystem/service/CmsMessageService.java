package com.workspace.llmsystem.service;

import com.workspace.llmsystem.model.CmsMessage;
import reactor.core.publisher.Flux;

import java.util.List;

public interface CmsMessageService {
    public Flux<String> chat(String prompt);
    public List<CmsMessage> getCompletedChatContent(Long sid);
}
