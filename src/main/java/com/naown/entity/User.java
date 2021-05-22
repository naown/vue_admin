package com.naown.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.ArrayList;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * @author chenjian
 * @since 2021-05-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
public class User extends BaseEntity {

    /** 用户名 */
    @NotNull(message = "用户名不能为空")
    private String username;

    /** 密码 */
    private String password;

    /** 头像 */
    private String avatar;

    private String phone;

    /** 邮箱 */
    @NotNull(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 城市 */
    private String city;

    /** 最后登录时间 */
    private Date lastLogin;

    @TableField(exist = false)
    private List<Role> roles = new ArrayList<>();
}
