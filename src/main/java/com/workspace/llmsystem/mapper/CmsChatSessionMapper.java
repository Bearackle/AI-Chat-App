package com.workspace.llmsystem.mapper;

import com.workspace.llmsystem.model.CmsChatSession;
import com.workspace.llmsystem.model.CmsChatSessionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CmsChatSessionMapper {
    long countByExample(CmsChatSessionExample example);

    int deleteByExample(CmsChatSessionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CmsChatSession row);

    int insertSelective(CmsChatSession row);

    List<CmsChatSession> selectByExample(CmsChatSessionExample example);

    CmsChatSession selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") CmsChatSession row, @Param("example") CmsChatSessionExample example);

    int updateByExample(@Param("row") CmsChatSession row, @Param("example") CmsChatSessionExample example);

    int updateByPrimaryKeySelective(CmsChatSession row);

    int updateByPrimaryKey(CmsChatSession row);
}