package com.ly.ttd.monitor.dto;

/**
 * @author yong.li
 * @since 2026/3/24 10:20
 */
public class ThreadPoolDto {
    private String poolName;
    private Integer corePoolSize;
    private Integer maximumPoolSize;
    private Integer queueSize;

    public ThreadPoolDto(String poolName, Integer corePoolSize, Integer maximumPoolSize, Integer queueSize) {
        this.poolName = poolName;
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.queueSize = queueSize;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(Integer maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public Integer getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }
}
