package com.ly.ttd.redis.consts;

/**
 * @author yong.li
 * @since 2026/3/9 15:15
 */
public enum RedisRunModeEnum {
    SINGLE("single", "单点模式"),
    SENTINEL("sentinel", "哨兵模式"),
    CLUSTER("cluster", "集群模式");
    private String mode;
    private String desc;

    private RedisRunModeEnum(String mode, String desc) {
        this.mode = mode;
        this.desc = desc;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
