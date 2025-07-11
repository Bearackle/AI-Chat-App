package com.workspace.llmsystem.common.exception;

import cn.hutool.core.util.StrUtil;
import com.workspace.llmsystem.common.api.CommonResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLSyntaxErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = ApiException.class)
    public CommonResult handle(ApiException e){
        if(e.getErrorCode() != null){
            return CommonResult.failed(e.getErrorCode());
        }
        return CommonResult.failed(e.getMessage());
    }
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResult handleValidException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if(bindingResult.hasErrors()){
            FieldError fieldError = bindingResult.getFieldError();
            if(fieldError != null){
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        return CommonResult.validateFailed(message);
    }
    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public CommonResult handleValidException(BindException e){
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField()+fieldError.getDefaultMessage();
            }
        }
        return CommonResult.validateFailed(message);
    }
    @ResponseBody
    @ExceptionHandler(value = SQLSyntaxErrorException.class)
    public CommonResult handleSQLSyntaxErrorException(SQLSyntaxErrorException e) {
        String message = e.getMessage();
        if (StrUtil.isNotEmpty(message) && message.contains("denied")) {
            message = "Môi trường trình diễn chưa có quyền sửa đổi.";
        }
        return CommonResult.failed(message);
    }
}
