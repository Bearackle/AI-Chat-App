package com.workspace.llmsystem.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotEmpty;
@Getter
@Setter
public class UmsUserParam {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    @Email
    private String email;
}
