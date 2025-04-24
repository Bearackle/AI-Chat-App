package com.workspace.llmsystem.service.impl;

import com.workspace.llmsystem.mapper.CmsChatSessionMapper;
import com.workspace.llmsystem.model.CmsChatSession;
import com.workspace.llmsystem.model.CmsChatSessionExample;
import com.workspace.llmsystem.model.UmsUser;
import com.workspace.llmsystem.service.CmsSessionService;
import com.workspace.llmsystem.service.UmsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CmsSessionServiceImpl implements CmsSessionService {
    @Autowired
    private UmsUserService umsUserService;
    @Autowired
    private CmsChatSessionMapper cmsChatSessionMapper;
    @Override
    public Long createSession() {
        CmsChatSession cmsChatSession = new CmsChatSession();
        UmsUser user = umsUserService.getCurrentUser();
        cmsChatSession.setUserId(user.getId());
        cmsChatSession.setCreatedAt(new Date());
        cmsChatSession.setUpdatedAt((new Date()));
        cmsChatSessionMapper.insertSelective(cmsChatSession);
        return cmsChatSession.getId();
    }
    @Override
    public int updateSessionName(Long id, String name) {
        CmsChatSession cmsChatSession = cmsChatSessionMapper.selectByPrimaryKey(id);
        cmsChatSession.setTitle(name);
        return cmsChatSessionMapper.updateByPrimaryKeySelective(cmsChatSession);
    }
    @Override
    public int deleteSession(Long sessionId) {
        return cmsChatSessionMapper.deleteByPrimaryKey(sessionId);
    }
    @Override
    public List<CmsChatSession> retriveHistories() {
        CmsChatSessionExample example = new CmsChatSessionExample();
        example.setOrderByClause("created_at desc");
        CmsChatSessionExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(umsUserService.getCurrentUser().getId());
        return cmsChatSessionMapper.selectByExample(example);
    }
}
