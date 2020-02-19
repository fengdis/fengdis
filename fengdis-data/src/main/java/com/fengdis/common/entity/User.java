package com.fengdis.common.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fengdis.util.Date2DTString;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * @version 1.0
 * @Descrittion:
 * @author: fengdi
 * @since: 2019/08/26 10:48
 */
@Entity
@Table(name = "tb_user")
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    public User(){}

    public static final Byte USER_STATUS_NOTACTIVE = 0;
    public static final Byte USER_STATUS_ENABLE = 1;
    public static final Byte USER_STATUS_DISABLE = 2;

    public static final Byte USER_LEVEL_COMMON = 0;
    public static final Byte USER_LEVEL_OFFICIAL = 1;

    public static final Byte USER_SEX_MALE = 0;
    public static final Byte USER_SEX_FAMALE = 1;

    public static final Byte USER_ISVALID_VALID_ = 0;
    public static final Byte USER_ISVALID_INVALID = 1;

    public User(String account, String password) {
        this.account = account;
        this.password = password;
    }

    @Id
    @GeneratedValue(generator = "DemoGenerator")
    @GenericGenerator(name = "DemoGenerator", strategy = "uuid")
    //@GenericGenerator(name = "DemoGenerator", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id",length = 32)
    @NotNull(message = "id不能为空",groups = Update.class)
    private String id;

    @NotBlank
    @Column(name = "account",length = 12, nullable = false)
    private String account;

    @NotBlank
    @Column(name = "password",length = 32, nullable = false)
    private String password;

    @Column(name = "birthdate",length = 10)
    private String birthdate;

    @Column(name = "sex")
    private Byte sex = USER_SEX_MALE;

    @Column(name = "realname",length = 10)
    private String realname = "";

    @Column(name = "avatar",length = 255)
    private String avatar = "";

    @Column(name = "id_no",length = 32)
    private String idNo;

    @Column(name = "phone",length = 32)
    private String phone;

    @Pattern(regexp = "([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}",message = "邮箱格式错误")
    @Column(name = "email",length = 32)
    private String email;

    @Column(name = "qq",length = 32)
    private String qq;

    @Column(name = "wechat",length = 32)
    private String wechat;

    @Column(name = "status")
    private Byte status = USER_STATUS_NOTACTIVE;

    @Column(name = "level")
    private Byte level = USER_LEVEL_COMMON;

    @Column(name = "integral", length = 10)
    private Integer integral = 0;

    @CreatedDate
    @JsonSerialize(using = Date2DTString.class)
    @Column(name = "create_date")
    private Date createDate;

    @LastModifiedDate
    @JsonSerialize(using = Date2DTString.class)
    @Column(name = "update_date")
    private Date updateDate;

    @Lob
    @Column(name = "remark")
    private String remark;

    @Column(name = "is_valid")
    private Byte isValid = USER_ISVALID_VALID_;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public Byte getSex() {
        return sex;
    }

    public void setSex(Byte sex) {
        this.sex = sex;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getLevel() {
        return level;
    }

    public void setLevel(Byte level) {
        this.level = level;
    }

    public Integer getIntegral() {
        return integral;
    }

    public void setIntegral(Integer integral) {
        this.integral = integral;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Byte getIsValid() {
        return isValid;
    }

    public void setIsValid(Byte isValid) {
        this.isValid = isValid;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", sex=" + sex +
                ", realname='" + realname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", idNo='" + idNo + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", qq='" + qq + '\'' +
                ", wechat='" + wechat + '\'' +
                ", status=" + status +
                ", level=" + level +
                ", integral=" + integral +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", remark='" + remark + '\'' +
                ", isValid=" + isValid +
                '}';
    }

    public @interface Update {}
}
