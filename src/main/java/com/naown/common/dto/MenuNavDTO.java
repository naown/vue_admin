package com.naown.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: chenjian
 * @since: 2021/5/18 23:29 周二
 **/
@Data
public class MenuNavDTO implements Serializable {
    /** ID */
    private Long id;
    /** 导航别名 */
    private String name;
    /** 导航名称 */
    private String title;
    /** 导航图标 */
    private String icon;
    /** 导航路径 */
    private String path;
    /** 懒加载导航路径 */
    private String component;
    /** 子路由 */
    private List<MenuNavDTO> children;
}
