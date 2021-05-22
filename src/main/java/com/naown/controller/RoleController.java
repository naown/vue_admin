package com.naown.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.naown.common.lang.Const;
import com.naown.common.lang.Result;
import com.naown.entity.Role;
import com.naown.entity.RoleMenu;
import com.naown.entity.UserRole;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenjian
 * @since 2021-05-09
 */
@RestController
@RequestMapping("/role")
public class RoleController extends BaseController {

    /**
     * 应该会废弃 --
     * @param id 角色Id
     * @return Result
     */
    @PreAuthorize("hasAnyAuthority('sys:role:list')")
    @GetMapping("/info/{id}")
    public Result info(@PathVariable(name = "id") Long id){
        Role role = roleService.getById(id);
        List<RoleMenu> roleMenus = roleMenuService.list(new QueryWrapper<RoleMenu>().eq("role_id", id));
        List<Long> menuIds = roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
        role.setMenuIds(menuIds);
        return Result.succeed(role);
    }

    /**
     * 获取角色列表 如果传了角色名则根据角色名查询
     * 拥有sys:role:list权限时才可以访问
     * @param name 角色名
     * @return Result
     */
    @PreAuthorize("hasAnyAuthority('sys:role:list')")
    @GetMapping("/list")
    public Result list(String name){
        Page<Role> rolePage = roleService.listPageRoleByName(getPage(),name);
        return Result.succeed(rolePage);
    }

    /**
     * 角色保存
     * 拥有sys:role:save权限时才可以保存
     * @param role 角色实体
     * @return Result
     */
    @PreAuthorize("hasAnyAuthority('sys:role:save')")
    @PostMapping("/save")
    public Result save(@Validated @RequestBody Role role){
        role.setCreated(LocalDateTime.now());
        role.setStatus(Const.STATUS_ON);
        roleService.save(role);
        return Result.succeed(role);
    }

    /**
     * 角色更新
     * 拥有sys:role:update权限时才可以更新
     * @param role 角色实体
     * @return Result
     */
    @PreAuthorize("hasAnyAuthority('sys:role:update')")
    @PostMapping("/update")
    public Result update(@Validated @RequestBody Role role){
        role.setUpdated(LocalDateTime.now());
        roleService.updateById(role);
        // 更新缓存
        userService.clearUserAuthorityInfoByRoleId(role.getId());
        return Result.succeed(role);
    }

    /**
     * 角色删除
     * 拥有sys:role:delete权限时才可以删除
     * @param roleIds 角色id集合
     * @return Result
     */
    @PreAuthorize("hasAnyAuthority('sys:role:delete')")
    @PostMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public Result delete(@RequestBody Long[] roleIds){

        roleService.removeByIds(Arrays.asList(roleIds));
        //删除中间表

        userRoleService.remove(new QueryWrapper<UserRole>().in("role_id",Arrays.asList(roleIds)));
        roleMenuService.remove(new QueryWrapper<RoleMenu>().in("role_id", Arrays.asList(roleIds)));

        // 删除缓存
        Arrays.stream(roleIds).forEach(id -> userService.clearUserAuthorityInfoByRoleId(id));
        return Result.succeed("删除成功");
    }

    /**
     * 分配角色菜单权限
     * 拥有sys:role:perm权限时才可以分配菜单权限
     * @param roleId 角色id
     * @param menuIds 菜单id集合
     * @return Result
     */
    @PreAuthorize("hasAnyAuthority('sys:role:perm')")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/perm/{roleId}")
    public Result perm(@PathVariable(name = "roleId") Long roleId, @RequestBody Long[] menuIds){

        List<RoleMenu> roleMenus = new ArrayList<>();
        Arrays.stream(menuIds).forEach(menuId -> {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleId);
            roleMenus.add(roleMenu);
        });

        roleMenuService.remove(new QueryWrapper<RoleMenu>().eq("role_id",roleId));
        roleMenuService.saveBatch(roleMenus);

        userService.clearUserAuthorityInfoByRoleId(roleId);
        return Result.succeed(menuIds);
    }
}
