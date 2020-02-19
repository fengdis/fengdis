package com.fengdis.common;

/**
 * @version 1.0
 * @Descrittion: 返回结果常量枚举
 * @author: fengdi
 * @since: 2019/08/30 17:26
 */
public enum ResultConstant {

    /**
     * 异常
     */
    FAILED("90000001", "系统错误"),
    /**
     * 成功
     */
    SUCCESS("00000000", "success"),
    /**
     * 未登录/token过期
     */
    UNAUTHORIZED("90000002", "获取登录用户信息失败"),
    /**
     * 失败
     */
    ERROR("90000000", "操作失败"),
    /**
     * 失败
     */
    PARAM_ERROR("90000003", "参数错误"),

    /**
     * 用户名或密码错误
     */
    INVALID_USERNAME_PASSWORD("10000003", "用户名或密码错误"),
    /**
     *
     */
    INVALID_RE_PASSWORD("10000010", "两次输入密码不一致"),
    /**
     * 用户名或密码错误
     */
    INVALID_PASSWORD("10000009", "旧密码错误"),
    /**
     * 用户名重复
     */
    USERNAME_ALREADY_IN("10000002", "用户已存在"),
    /**
     * 用户不存在
     */
    INVALID_USER("10000001", "用户不存在"),
    /**
     * 角色不存在
     */
    INVALID_ROLE("10000004", "角色不存在"),

    /**
     * 角色不存在
     */
    ROLE_USER_USED("10000008", "角色使用中，不可删除"),

    /**
     * 参数错误-已存在
     */
    INVALID_PARAM_EXIST("10000005", "请求参数已存在"),
    /**
     * 参数错误
     */
    INVALID_PARAM_EMPTY("10000006", "请求参数为空"),
    /**
     * 没有权限
     */
    USER_NO_PERMITION("10000007", "当前用户无该接口权限"),
    /**
     * 校验码错误
     */
    VERIFY_PARAM_ERROR("10000011", "校验码错误"),
    /*
     * 校验码过期
     */
    VERIFY_PARAM_PASS("10000012", "校验码过期"),

    /**
     * 用户没有添加、删除评论或回复的权限
     */
    USER_NO_AUTHORITY("10000013","该用户没有权限"),

    /**
     * 用户没有添加、删除评论或回复的权限
     */
    MOBILE_ERROR("10000014","手机号格式错误") ,
    /**
     * 数据更新或增加失败
     */
    DATA_ERROR("10000015","数据操作错误")
    ;


    public String result;
    public String msg;

    ResultConstant(String result, String msg) {
        this.result = result;
        this.msg = msg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
