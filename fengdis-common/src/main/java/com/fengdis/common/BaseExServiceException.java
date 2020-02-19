package com.fengdis.common;

/**
 * @version 1.0
 * @Descrittion: 异常基类
 * @author: fengdi
 * @since: 2019/08/30 17:26
 */
@SuppressWarnings("serial")
public class BaseExServiceException extends RuntimeException {

    public static final int UNKNOW_EXCEPTION = -2;// 未知异常
    public static final int SYSTEM_EXCEPTION = -1;// 系统异常
    public static final int SERVICE_EXCEPTION = 0;// 服务异常
    public static final int NETWORK_EXCEPTION = 1;// 网络异常

    protected String message;
    protected int code = 0;

    public BaseExServiceException() {
        super();
    }

    public BaseExServiceException(String message, int code) {
        super();
        this.message = message;
        this.code = code;
    }

    public BaseExServiceException(String message) {
        super();
        this.message = message;
    }

    public BaseExServiceException(Throwable cause) {
        super(cause);
    }

    public BaseExServiceException(int code) {
        super();
        this.code = code;
    }

    public BaseExServiceException(String message, Throwable cause) {
        super(message, cause);
        this.message=message;
    }

    public BaseExServiceException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message=message;
    }

    public BaseExServiceException(int code, String message) {
        super(message);
        this.code = code;
        this.message=message;
    }

    public BaseExServiceException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isServiceException() {
        return this.code == SERVICE_EXCEPTION;
    }

    public boolean isSystemException() {
        return this.code == SYSTEM_EXCEPTION;
    }
}

