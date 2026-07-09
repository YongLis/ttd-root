package com.ly.ttd.base.result;


import java.util.List;

/**
 * @author yong.li
 * @since 2026/4/13 19:20
 */
public class PageResult<T> {

    private Long total;

    private Long current;

    private Long pageSize;

    private String code;

    private String message;

    private List<T> data;

    public static <T> PageResult<T> success(List<T> data) {
        PageResult<T> result = new PageResult<>();
        result.setCode("0000");
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> PageResult<T> success() {
        return success(null);
    }

    public static <T> PageResult<T> error(String message) {
        PageResult<T> result = new PageResult<>();
        result.setCode("9999");
        result.setMessage(message);
        return result;
    }

    public static <T> PageResult<T> error(String code, String message) {
        PageResult<T> result = new PageResult<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
