package com.workspace.llmsystem.dao;

import com.workspace.llmsystem.model.CmsMessage;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface CmsMessageDao {
    List<CmsMessage> getFullContentBySession(@Param("sessionId") Long sessionId);
}
