package com.fengdis.vo;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class RedisVo implements Serializable {

    @NotBlank
    private String key;

    @NotBlank
    private String value;

    public RedisVo(String key,String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RedisVo{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
