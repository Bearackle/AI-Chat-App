package com.workspace.llmsystem.controller;

import com.workspace.llmsystem.common.api.CommonResult;
import com.workspace.llmsystem.dto.UmsUserLoginParam;
import com.workspace.llmsystem.dto.UmsUserParam;
import com.workspace.llmsystem.model.UmsUser;
import com.workspace.llmsystem.service.UmsUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@Tag(name = "UmsUserController", description = "User management system")
@RequestMapping("/user")
public class UmsUserController {
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private UmsUserService  userService;
    @Operation(summary = "Register")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<UmsUser> register(@Validated @RequestBody UmsUserParam umsUserParam){
        UmsUser umsUser = userService.register(umsUserParam);
        if(umsUser == null){
            return CommonResult.failed();
        }
        return CommonResult.success(umsUser);
    }
    @Operation(summary = "dang nhap")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Map<String,String>> login(@Validated @RequestBody UmsUserLoginParam userLoginParam){
        String token = userService.login(userLoginParam.getUsername(), userLoginParam.getPassword());
        if (token == null) {
            return CommonResult.validateFailed("failed to login");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }
    @Operation(summary = "tìm hồ sơ")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String,Object>> getUserInfo(Principal principal) {
        if(principal==null){
            return CommonResult.unauthorized(null);
        }
        String username = principal.getName();
        UmsUser umsUser = userService.getUserByUsername(username);
        Map<String, Object> data = new HashMap<>();
        data.put("username", umsUser.getUsername());
        return CommonResult.success(data);
    }
}
