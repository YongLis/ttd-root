package com.ly.ttd.monitor.dto;

/**
 * @author yong.li
 * @since 2026/3/23 15:35
 */
public class MonitorDto {
    /**
     * 指标编码
     */
    private String code;
    /**
     * 指标名称
     */
    private String name;
    /**
     * 值
     */
    private Double value;
    /**
     * 单位
     */
    private String unit;

    public MonitorDto(String code, String name, Double value, String unit) {
        this.code = code;
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
