package com.lh.sms.client.framing.exceptions;

/**
 * 自定义异常
 */
public class MsgException extends RuntimeException {

    private Integer code;

    public MsgException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }
    public MsgException(Integer code) {
        super(String.valueOf(System.currentTimeMillis()));
        this.code = code;
    }
    public MsgException(String msg) {
        super(msg);
    }

    public MsgException(String msg, Object... obj) {
        super(String.format(msg, obj));
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
