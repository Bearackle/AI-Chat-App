package com.workspace.llmsystem.service;

import com.workspace.llmsystem.dto.UmsUserParam;
import com.workspace.llmsystem.dto.UpdatePasswordParam;
import com.workspace.llmsystem.model.UmsUser;
import org.apache.ibatis.javassist.compiler.ast.Keyword;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UmsUserService {
    UmsUser getUserByUsername(String username);
    UmsUser register(UmsUserParam umsUserParam);
    String login(String username, String password);
    UmsUser getItem(Long id);
    List<UmsUser> list(String keyword, Integer pageSize, Integer pageNum);
    int update (Long id, UmsUser user);
    int delete(Long id);
    int updatePassword(UpdatePasswordParam updatePasswordParam);
    UserDetails loadUserByUsername(String username);
    UmsUserCacheService getCacheService();
    void logout(String username);
    UmsUser getCurrentUser();
}
