package com.ly.ttd.base.exception;

/**
 * @author yong.li
 * @since 2026/7/9 21:20
 */
public class BizException extends RuntimeException {

    private String code;
    private String message;

    public BizException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BizException(String message) {
        super(message);
        this.code = "-999999";
        this.message = message;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
