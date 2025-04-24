package com.workspace.llmsystem.common.api;

public enum ResultCode implements IErrorCode {
    SUCCESS(200, "Hoạt động thành công"),
    FAILED(500, "Hoạt động không thành công"),
    VALIDATE_FAILED(404, "Kiểm tra tham số không thành công"),
    UNAUTHORIZED(401, "token không hợp lệ"),
    FORBIDDEN(403, "Không có quyền liên quan");
    private long code;
    private String message;
    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }
    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
