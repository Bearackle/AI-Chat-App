package com.workspace.llmsystem.dao;

import com.workspace.llmsystem.model.CmsChatSession;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ChatSessionDao {
    List<CmsChatSession> getListSessions(@Param("userId") Long userId);
}
