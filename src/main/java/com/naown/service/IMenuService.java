package com.naown.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.naown.common.dto.MenuNavDTO;
import com.naown.entity.Menu;

import java.util.List;

/**
 * @author chenjian
 * @since 2021-05-09
 */
public interface IMenuService extends IService<Menu> {

    /**
     * 获取当前用户的导航栏
     * @return
     */
    List<MenuNavDTO> getCurrentUserNav();
    
    /**
     * 获取所有菜单信息并且转换成树状结构
     * @return
     */
    List<Menu> tree();

    /**
     * 查询该菜单Id下是否拥有子集
     * @param id
     * @return
     */
    Integer count(Long id);

}
