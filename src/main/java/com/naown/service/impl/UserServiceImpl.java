package com.naown.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.naown.entity.Menu;
import com.naown.entity.Role;
import com.naown.entity.User;
import com.naown.mapper.UserMapper;
import com.naown.service.IMenuService;
import com.naown.service.IRoleService;
import com.naown.service.IUserService;
import com.naown.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenjian
 * @since 2021-05-09
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IMenuService menuService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public User getByUserName(String username) {
        return this.getOne(new QueryWrapper<User>().eq("username",username));
    }

    @Override
    public String getUserAuthorityInfo(Long userId) {

        User user = this.getById(userId);
        String authority = "";

        if (redisUtil.hasKey("GrantedAuthority"+user.getUsername())){
            return (String)redisUtil.get("GrantedAuthority"+user.getUsername());
        }
        // 获取角色权限
        List<Role> roles = roleService.listRoleById(userId);
        if (roles.size() > 0){
            authority = (roles.stream().map(role -> "ROLE_" + role.getCode()).collect(Collectors.joining(","))).concat(",");
        }
        // 获取导航菜单操作编码
        List<Long> menuIds = userMapper.listNavMenuIds(userId);
        if (menuIds.size() > 0){
            List<Menu> menus = menuService.listByIds(menuIds);
            String menuPerms = menus.stream().map(Menu::getPerms).collect(Collectors.joining(","));
            authority = authority.concat(menuPerms);
        }
        redisUtil.set("GrantedAuthority"+user.getUsername(),authority,60 * 60);
        return authority;
    }

    @Override
    public List<User> listUserByRoleId(Long roleId) {
        return this.list(new QueryWrapper<User>().inSql("id","select user_id from sys_user_role where role_id =" + roleId));
    }

    @Override
    public void clearUserAuthorityInfo(String username) {
        redisUtil.del("GrantedAuthority" + username);
    }

    @Override
    public void clearUserAuthorityInfoByRoleId(Long roleId) {
        List<User> users = listUserByRoleId(roleId);
        users.forEach(user -> this.clearUserAuthorityInfo(user.getUsername()));
    }

    @Override
    public void clearUserAuthorityInfoByMenuId(Long menuId) {
        List<User> users = userMapper.listUserByMenuId(menuId);
        users.forEach(user -> this.clearUserAuthorityInfo(user.getUsername()));
    }
}
