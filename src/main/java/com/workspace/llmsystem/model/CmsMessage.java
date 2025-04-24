package com.workspace.llmsystem.model;

import java.io.Serializable;
import java.util.Date;

public class CmsMessage implements Serializable {
    private Long id;

    private Long sessionId;

    private Long userId;

    private Integer isUser;

    private Date createdAt;

    private String content;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getIsUser() {
        return isUser;
    }

    public void setIsUser(Integer isUser) {
        this.isUser = isUser;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sessionId=").append(sessionId);
        sb.append(", userId=").append(userId);
        sb.append(", isUser=").append(isUser);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", content=").append(content);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}