package com.ly.ttd.base.result;


/**
 * @author yong.li
 * @since 2026/4/13 13:50
 */
public class PageQuery extends BaseRequest {

    private long pageSize;

    private long current;


    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }
}
