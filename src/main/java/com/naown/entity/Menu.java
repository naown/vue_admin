package com.naown.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.ArrayList;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author chenjian
 * @since 2021-05-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_menu")
public class Menu extends BaseEntity{

    /** 父菜单ID，一级菜单为0  */
    @NotNull(message = "上级菜单不能为空")
    private Long parentId;

    /** 菜单名 */
    @NotNull(message = "菜单名称不能为空")
    private String name;

    /** 菜单URL */
    private String path;

    /** 授权(多个用逗号分隔，如：user:list,user:create) */
    @NotNull(message = "菜单授权码不能为空")
    private String perms;

    private String component;

    /** 类型     0：目录   1：菜单   2：按钮 */
    @NotNull(message = "菜单类型不能为空")
    private Integer type;

    /** 菜单图标 */
    private String icon;

    /** 排序 */
    private Integer orderNum;

    @TableField(exist = false)
    private List<Menu> children = new ArrayList<>();
}
