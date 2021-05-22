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

import javax.validation.constraints.NotNull;

/**
 * @author chenjian
 * @since 2021-05-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_role")
public class Role extends BaseEntity {

    /** 角色名 */
    @NotNull(message = "角色名称不能为空")
    private String name;

    /** 角色唯一编码 */
    @NotNull(message = "角色编码不能为空")
    private String code;

    /** 备注 */
    private String description;

    /** 菜单Id集合 */
    @TableField(exist = false)
    private List<Long> menuIds = new ArrayList<>();
}
