package com.workspace.llmsystem.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.workspace.llmsystem.bo.UserUserDetails;
import com.workspace.llmsystem.common.exception.Assert;
import com.workspace.llmsystem.dto.UmsUserParam;
import com.workspace.llmsystem.dto.UpdatePasswordParam;
import com.workspace.llmsystem.mapper.UmsUserMapper;
import com.workspace.llmsystem.model.UmsUser;
import com.workspace.llmsystem.model.UmsUserExample;
import com.workspace.llmsystem.security.util.JwtTokenUtil;
import com.workspace.llmsystem.security.util.SpringUtil;
import com.workspace.llmsystem.service.UmsUserCacheService;
import com.workspace.llmsystem.service.UmsUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UmsUserServiceImpl implements UmsUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsUserServiceImpl.class);
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UmsUserMapper umsUserMapper;
    private UmsUserService umsUserService;

    @Override
    public UmsUser getUserByUsername(String username) {
        UmsUser user = getCacheService().getUser(username);
        if(user != null) return user;
        UmsUserExample example = new UmsUserExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<UmsUser> users = umsUserMapper.selectByExample(example);
        if(users != null && users.size() > 0){
            user = users.getFirst();
            getCacheService().setUser(user);
            return user;
        }
        return null;
    }

    @Override
    public UmsUser register(UmsUserParam umsUserParam) {
        UmsUser user = new UmsUser();
        BeanUtils.copyProperties(umsUserParam, user);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setStatus(1);
        UmsUserExample example = new UmsUserExample();
        example.createCriteria().andUsernameEqualTo(user.getUsername());
        List<UmsUser> users = umsUserMapper.selectByExample(example);
        if(users != null && users.size() > 0){
            return null;
        }
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        umsUserMapper.insert(user);
        return user;
    }
    @Override
    public String login(String username, String password) {
        String token = null;
        try{
            UserDetails userDetails = loadUserByUsername(username);
            if(!passwordEncoder.matches(password, userDetails.getPassword())){
                Assert.fail("wrong password");
            }
            if(!userDetails.isEnabled()){
                Assert.fail("user not enabled");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        }catch (AuthenticationException e) {
            LOGGER.warn("Failed:{}", e.getMessage());
        }
        return token;
    }

    @Override
    public UmsUser getItem(Long id) {
        return umsUserMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<UmsUser> list(String keyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        UmsUserExample example = new UmsUserExample();
        UmsUserExample.Criteria criteria = example.createCriteria();
        if(!StrUtil.isEmpty(keyword)){
            criteria.andUsernameLike("%"+keyword+"%");
        }
        return umsUserMapper.selectByExample(example);
    }
    @Override
    public int update(Long id, UmsUser user) {
        user.setId(id);
        UmsUser oldUser = umsUserMapper.selectByPrimaryKey(id);
        if(oldUser.getPassword().equals(user.getPassword())){
            user.setPassword(null);
        }else{
            if(StrUtil.isEmpty(user.getPassword())){
                user.setPassword(null);
            }else{
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }
        int count = umsUserMapper.updateByPrimaryKeySelective(user);
        getCacheService().deleteUser(id);
        return count;
    }

    @Override
    public int delete(Long id) {
        int count = umsUserMapper.deleteByPrimaryKey(id);
        getCacheService().deleteUser(id);
        return count;
    }

    @Override
    public int updatePassword(UpdatePasswordParam param) {
        if(StrUtil.isEmpty(param.getUsername())
                ||StrUtil.isEmpty(param.getOldPassword())
                ||StrUtil.isEmpty(param.getNewPassword())){
            return -1;
        }
        UmsUserExample example = new UmsUserExample();
        example.createCriteria().andUsernameEqualTo(param.getUsername());
        List<UmsUser> users = umsUserMapper.selectByExample(example);
        if(CollUtil.isEmpty(users)){
            return -2;
        }
        UmsUser umsUser = users.get(0);
        if(!passwordEncoder.matches(param.getOldPassword(),umsUser.getPassword())){
            return -3;
        }
        umsUser.setPassword(passwordEncoder.encode(param.getNewPassword()));
        umsUserMapper.updateByPrimaryKey(umsUser);
        getCacheService().deleteUser(umsUser.getId());
        return 1;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UmsUser user = getUserByUsername(username);
        if( user != null) {
            return new UserUserDetails(user);
        }
        throw new UsernameNotFoundException("User not found!!!");
    }

    @Override
    public UmsUserCacheService getCacheService() {
        return SpringUtil.getBean(UmsUserCacheService.class);
    }

    @Override
    public void logout(String username) {
        UmsUser admin = getCacheService().getUser(username);
        getCacheService().deleteUser(admin.getId());
    }
    @Override
    public UmsUser getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserUserDetails userDetails = (UserUserDetails) authentication.getPrincipal();
        return userDetails.getUmsUser();
    }
}
