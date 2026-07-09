package com.ly.ttd.monitor;

/**
 * @author yong.li
 * @since 2026/3/24 13:04
 */
public enum TypeEnum {
    JVM("jvm", "jvm内存"),
    THREAD_POOL("threadPool", "线程池")
    ;

    private String code;
    private String desc;

    private TypeEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
