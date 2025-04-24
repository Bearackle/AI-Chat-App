package com.workspace.llmsystem.service;

import com.workspace.llmsystem.model.UmsUser;

public interface UmsUserCacheService {
    void deleteUser(Long id);
    UmsUser getUser(String username);
    void setUser(UmsUser user);
}
