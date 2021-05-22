package com.naown.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.naown.entity.User;

import java.util.List;

/**
 * @author chenjian
 * @since 2021-05-09
 */
public interface IUserService extends IService<User> {

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    User getByUserName(String username);

    /**
     * 根据用户id查询角色的权限和操作按钮权限
     * @param userId
     * @return
     */
    String getUserAuthorityInfo(Long userId);

    /**
     * 根据角色Id获取用户列表
     * @param roleId
     * @return
     */
    List<User> listUserByRoleId(Long roleId);

    /**
     * 根据用户名清除权限缓存
     * @param username
     */
    void clearUserAuthorityInfo(String username);

    /**
     * 根据角色id清除用户缓存
     * @param roleId
     */
    void clearUserAuthorityInfoByRoleId(Long roleId);

    /**
     *  根据菜单id清除菜单缓存
     * @param menuId
     */
    void clearUserAuthorityInfoByMenuId(Long menuId);
}
