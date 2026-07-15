package com.ly.ttd.utils;

import com.google.common.collect.Lists;
import org.apache.commons.jxpath.JXPathContext;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yong.li
 * @since 2026/7/8 10:32
 */
public class DataPath {
    private JXPathContext jxPathContext;

    public DataPath(String json) {
        this.jxPathContext = JXPathContext.newContext(GenericStaticFunctions.toMap(json));
        this.jxPathContext.setLenient(true);
    }

    public DataPath(Map<String, Object> map) {
        this.jxPathContext = JXPathContext.newContext(map);
        this.jxPathContext.setLenient(true);
    }

    public Boolean exists(String path) {
        return this.jxPathContext.getValue(path) != null;
    }

    public String getString(String path) {
        return this.getString(path, "");
    }

    public String getString(String path, String defValue) {
        return GenericStaticFunctions.getString(this.jxPathContext.getValue(path), defValue);
    }

    public Integer getInt(String path) {
        return this.getInt(path, 0);
    }

    public Integer getInt(String path, Integer defValue) {
        return GenericStaticFunctions.getInt(this.jxPathContext.getValue(path), defValue);
    }

    public BigDecimal getBigDecimal(String path) {
        return this.getBigDecimal(path, BigDecimal.ZERO);
    }

    public BigDecimal getBigDecimal(String path, BigDecimal defValue) {
        return GenericStaticFunctions.getBigDecimal(this.jxPathContext.getValue(path), defValue);
    }

    public BigDecimal getBigDecimal(String path, int scale, BigDecimal defValue) {
        return GenericStaticFunctions.getBigDecimal(this.jxPathContext.getValue(path), scale, defValue);
    }

    public Float getFloat(String path) {
        return this.getFloat(path, 0.0F);
    }

    public Float getFloat(String path, Float defValue) {
        return GenericStaticFunctions.getFloat(this.jxPathContext.getValue(path), defValue);
    }

    public Float getFloat(String path, int scale, Float defValue) {
        return GenericStaticFunctions.getFloat(this.jxPathContext.getValue(path), scale, defValue);
    }

    public Short getShort(String path) {
        return this.getShort(path, Short.valueOf((short) 0));
    }

    public Short getShort(String path, Short defValue) {
        return GenericStaticFunctions.getShort(this.jxPathContext.getValue(path), defValue);
    }

    public Double getDouble(String path) {
        return this.getDouble(path, 0.0);
    }

    public Double getDouble(String path, Double defValue) {
        return GenericStaticFunctions.getDouble(this.jxPathContext.getValue(path), defValue);
    }

    public Double getDouble(String path, int scale, Double defValue) {
        return GenericStaticFunctions.getDouble(this.jxPathContext.getValue(path), scale, defValue);
    }

    public Long getLong(String path) {
        return this.getLong(path, 0L);
    }

    public Long getLong(String path, Long defValue) {
        return GenericStaticFunctions.getLong(this.jxPathContext.getValue(path), defValue);
    }

    public Date getDate(String path) {
        return GenericStaticFunctions.getDate(this.jxPathContext.getValue(path), (Date) null);
    }

    public Date getDate(String path, Date defValue) {
        return GenericStaticFunctions.getDate(this.jxPathContext.getValue(path), defValue);
    }

    public Boolean getBoolean(String path) {
        return GenericStaticFunctions.getBoolean(this.jxPathContext.getValue(path), Boolean.FALSE);
    }

    public Boolean getBoolean(String path, Boolean defValue) {
        return GenericStaticFunctions.getBoolean(this.jxPathContext.getValue(path), defValue);
    }

    public List getList(String path) {
        Iterator iterate = this.jxPathContext.iterate(path);
        return Lists.newArrayList(iterate);
    }
}
