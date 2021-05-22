package com.naown.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.naown.entity.Role;

import java.util.List;

/**
 * @author chenjian
 * @since 2021-05-09
 */
public interface IRoleService extends IService<Role> {

    /**
     * 根据用户id查询角色
     * @param userId
     * @return
     */
    List<Role> listRoleById(Long userId);

    /**
     * 根据角色名查询
     * @param page
     * @param name
     * @return
     */
    Page<Role> listPageRoleByName(Page page, String name);

    /**
     * 根据用户id查询角色
     * @param userId
     * @return
     */
    List<Role> listRolesByUserId(Long userId);
}
