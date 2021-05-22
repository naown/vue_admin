package com.naown.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.naown.entity.User;

import java.util.List;

/**
 * @author chenjian
 * @since 2021-05-09
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户id查询菜单id列表 导航菜单栏
     * @param userId
     * @return
     */
    List<Long> listNavMenuIds(Long userId);

    /**
     * 根据菜单id查询用户列表
     * @param menuId
     * @return
     */
    List<User> listUserByMenuId(Long menuId);
}
