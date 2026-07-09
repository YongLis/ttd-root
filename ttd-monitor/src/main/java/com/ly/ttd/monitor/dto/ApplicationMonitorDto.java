package com.ly.ttd.monitor.dto;

import java.util.Map;

/**
 * @author yong.li
 * @since 2026/3/24 10:52
 */
public class ApplicationMonitorDto {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 机器host
     */
    private String host;

    /**
     * 端口
     */
    private int  port;

    /**
     * 机器码
     */
    private String mid;

    /**
     * 监控类型
     */
    private String monitorType;

    /**
     * 监控数据
     */
    private Map<String, Object> data;


    public ApplicationMonitorDto() {
    }

    public ApplicationMonitorDto(String appName, String host, int port, String mid, String monitorType, Map<String, Object> data) {
        this.appName = appName;
        this.host = host;
        this.port = port;
        this.mid = mid;
        this.monitorType = monitorType;
        this.data = data;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(String monitorType) {
        this.monitorType = monitorType;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
