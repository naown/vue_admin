package com.naown.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.naown.common.dto.MenuNavDTO;
import com.naown.entity.Menu;
import com.naown.entity.User;
import com.naown.mapper.MenuMapper;
import com.naown.mapper.UserMapper;
import com.naown.service.IMenuService;
import com.naown.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenjian
 * @since 2021-05-09
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper,Menu> implements IMenuService {
    @Autowired
    private IUserService userService;
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<MenuNavDTO> getCurrentUserNav() {
        String username = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getByUserName(username);
        List<Long> menuIds = userMapper.listNavMenuIds(user.getId());
        List<Menu> menus = this.listByIds(menuIds);

        // 转树状结构
        List<Menu> menuTree = buildTreeMenu(menus);
        // 实体转DTO
        return convert(menuTree);
    }

    @Override
    public List<Menu> tree() {
        List<Menu> menus = this.list(new QueryWrapper<Menu>().orderByAsc("order_num"));
        return buildTreeMenu(menus);
    }


    @Override
    public Integer count(Long id) {
        return this.count(new QueryWrapper<Menu>().eq("parent_id", id));
    }

    private List<MenuNavDTO> convert(List<Menu> menuTree) {
        List<MenuNavDTO> menuNavs = new ArrayList<>();
        menuTree.forEach(menu -> {
            MenuNavDTO menuNavDTO = new MenuNavDTO();
            menuNavDTO.setId(menu.getId());
            menuNavDTO.setName(menu.getPerms());
            menuNavDTO.setTitle(menu.getName());
            menuNavDTO.setPath(menu.getPath());
            menuNavDTO.setIcon(menu.getIcon());
            menuNavDTO.setComponent(menu.getComponent());

            if (menu.getChildren().size() >0){
                menuNavDTO.setChildren(convert(menu.getChildren()));
            }
            menuNavs.add(menuNavDTO);
        });
        return menuNavs;
    }

    private List<Menu> buildTreeMenu(List<Menu> menus) {
        List<Menu> newMenus = new ArrayList<>();
        for (Menu menu : menus) {
            for (Menu item : menus) {
                if (menu.getId().equals(item.getParentId())){
                    menu.getChildren().add(item);
                }
            }
            if (menu.getParentId() == 0L){
                newMenus.add(menu);
            }
        }
        return newMenus;
    }
}
