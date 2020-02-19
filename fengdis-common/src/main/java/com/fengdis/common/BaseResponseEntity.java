package com.fengdis.common;

/**
 * @version 1.0
 * @Descrittion: 返回数据基类
 * @author: fengdi
 * @since: 2019/08/30 17:26
 */
public class BaseResponseEntity<T> {
    private boolean status;
    private String msg;
    private T data;

    public BaseResponseEntity() {
    }

    public BaseResponseEntity(boolean status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public BaseResponseEntity(boolean status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
