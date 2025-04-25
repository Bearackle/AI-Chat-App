package com.workspace.llmsystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmsMessageParam {
    private Long sessionId;
    private String content;
}
