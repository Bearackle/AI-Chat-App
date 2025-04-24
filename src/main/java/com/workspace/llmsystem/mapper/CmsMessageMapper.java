package com.workspace.llmsystem.mapper;

import com.workspace.llmsystem.model.CmsMessage;
import com.workspace.llmsystem.model.CmsMessageExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CmsMessageMapper {
    long countByExample(CmsMessageExample example);

    int deleteByExample(CmsMessageExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CmsMessage row);

    int insertSelective(CmsMessage row);

    List<CmsMessage> selectByExampleWithBLOBs(CmsMessageExample example);

    List<CmsMessage> selectByExample(CmsMessageExample example);

    CmsMessage selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") CmsMessage row, @Param("example") CmsMessageExample example);

    int updateByExampleWithBLOBs(@Param("row") CmsMessage row, @Param("example") CmsMessageExample example);

    int updateByExample(@Param("row") CmsMessage row, @Param("example") CmsMessageExample example);

    int updateByPrimaryKeySelective(CmsMessage row);

    int updateByPrimaryKeyWithBLOBs(CmsMessage row);

    int updateByPrimaryKey(CmsMessage row);
}