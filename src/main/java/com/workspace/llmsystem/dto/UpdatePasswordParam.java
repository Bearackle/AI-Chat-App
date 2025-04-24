package com.workspace.llmsystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordParam {
    private String username;
    private String oldPassword;
    private String newPassword;
}
