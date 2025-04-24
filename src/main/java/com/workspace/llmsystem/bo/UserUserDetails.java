package com.workspace.llmsystem.bo;

import com.workspace.llmsystem.model.UmsUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserUserDetails implements UserDetails {
    private final UmsUser umsUser;
    public UserUserDetails(UmsUser umsUser) {
        this.umsUser = umsUser;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return umsUser.getPassword();
    }

    @Override
    public String getUsername() {
        return umsUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return umsUser.getStatus().equals(1);
    }
    public UmsUser getUmsUser() {
        return umsUser;
    }
}
