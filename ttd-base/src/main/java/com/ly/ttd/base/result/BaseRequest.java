package com.ly.ttd.base.result;


/**
 * @author yong.li
 * @since 2026/4/13 10:45
 */
public class BaseRequest {
    private Long projectId;

    private String sessionId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
