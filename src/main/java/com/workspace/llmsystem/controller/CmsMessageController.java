package com.workspace.llmsystem.controller;

import com.workspace.llmsystem.common.api.CommonResult;
import com.workspace.llmsystem.dto.CmsMessageParam;
import com.workspace.llmsystem.model.CmsMessage;
import com.workspace.llmsystem.service.CmsMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping(value = "/conversation")
public class CmsMessageController {
    @Autowired
    private CmsMessageService cmsMessageService;
    private final Logger LOGGER = LoggerFactory.getLogger(CmsMessageController.class);

    @RequestMapping(value = "/chat", method = RequestMethod.POST, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<String> chat(@RequestBody CmsMessageParam cmsMessageParam) {
        Flux<String> data = cmsMessageService.chat(cmsMessageParam.getContent(), cmsMessageParam.getSessionId());
        return data;
    }
    @RequestMapping(value = "/chat/{ssid}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<CmsMessage>> chat(@PathVariable(value = "ssid") Long sessionId) {
        return CommonResult.success(cmsMessageService.getCompletedChatContent(sessionId));
    }
}
