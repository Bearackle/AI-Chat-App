package com.workspace.llmsystem.common.exception;


import com.workspace.llmsystem.common.api.IErrorCode;

public class Assert {
    public static void fail(String message) {
        throw new ApiException(message);
    }

    public static void fail(IErrorCode errorCode) {
        throw new ApiException(errorCode);
    }
}
