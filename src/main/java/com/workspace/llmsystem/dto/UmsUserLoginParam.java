package com.workspace.llmsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UmsUserLoginParam {
    @NotEmpty
    @Schema($schema = "username",required = true)
    private String username;
    @NotEmpty
    @Schema( $schema= "mat khau",required = true)
    private String password;
}
