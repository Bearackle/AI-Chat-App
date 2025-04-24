package com.workspace.llmsystem.service;

import com.workspace.llmsystem.model.CmsChatSession;
import com.workspace.llmsystem.model.CmsMessage;

import java.util.List;

public interface CmsSessionService {
    public Long createSession();
    public int updateSessionName(Long id,String name);
    public int deleteSession(Long sessionId);
    public List<CmsChatSession> retriveHistories();
}
