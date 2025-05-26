package com.workspace.llmsystem.controller;

import com.workspace.llmsystem.common.api.CommonResult;
import com.workspace.llmsystem.common.api.ResultCode;
import com.workspace.llmsystem.dto.CmsChatSessionParam;
import com.workspace.llmsystem.model.CmsChatSession;
import com.workspace.llmsystem.service.CmsSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/conversations")
public class CmsChatSessionController {
    private final Logger LOGGER = Logger.getLogger(CmsChatSessionController.class.getName());
    @Autowired
    private CmsSessionService cmsSessionService;

    @RequestMapping(value ="/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Map<String, Long>> create(){
        Long id = cmsSessionService.createSession();
        Map<String, Long> data = new HashMap<>();
        data.put("sessionId", id);
        LOGGER.info("key : sessionId value:" + id);
        return CommonResult.success(data);
    }
    @RequestMapping(value = "/update-name/{id}", method = RequestMethod.PATCH)
    @ResponseBody
    public CommonResult<Map<String, Boolean>> updateSessionName(@PathVariable Long id,
                                                                @RequestBody CmsChatSessionParam param){
        int row = cmsSessionService.updateSessionName(id,param.getTitle());
        Map<String, Boolean> data = new HashMap<>();
        if(row > 0) {
            data.put("updated", true);
            return CommonResult.success(data);
        }
        data.put("updated", false);
        return CommonResult.failed(ResultCode.FAILED);
    }
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public CommonResult<Map<String,Boolean>> deleteSession(@PathVariable Long id){
        int row = cmsSessionService.deleteSession(id);
        Map<String,Boolean> data = new HashMap<>();
        if(row > 0)
            data.put("deleted", true);
        else
            data.put("deleted", false);
        return CommonResult.success(data);
    }
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<CmsChatSession>> get(){
        List<CmsChatSession> sessions = cmsSessionService.retriveHistories();
        return CommonResult.success(sessions);
    }
}
