package com.naown.controller;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.naown.common.dto.MenuNavDTO;
import com.naown.common.lang.Result;
import com.naown.entity.Menu;
import com.naown.entity.RoleMenu;
import com.naown.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author chenjian
 * @since 2021-05-09
 */
@RestController
@RequestMapping("/menu")
public class MenuController extends BaseController {

    /**
     * 获取导航栏菜单
     * @param principal security
     * @return Result
     */
    @GetMapping("/nav")
    public Result getNav(Principal principal){
        long currentTimeMillis = System.currentTimeMillis();
        User user = userService.getByUserName(principal.getName());

        // 获取权限信息
        String authorityInfo = userService.getUserAuthorityInfo(user.getId());
        String[] authorityInfoArray = StringUtils.tokenizeToStringArray(authorityInfo, ",");

        // 获取导航栏信息
        List<MenuNavDTO> navs = menuService.getCurrentUserNav();

        long timeMillis = System.currentTimeMillis();
        System.out.println("共执行"+((timeMillis - currentTimeMillis))+ "豪秒");
        return Result.succeed(MapUtil.builder()
                .put("authorizations",authorityInfoArray)
                .put("nav",navs)
                .map()
        );
    }

    /**
     * 根据Id查询菜单
     * 拥有sys:menu:list权限时才可以获取
     * @param id 菜单Id
     * @return Result
     */
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result info(@PathVariable(name = "id") Long id) {
        return Result.succeed(menuService.getById(id));
    }

    /**
     * 获取所有菜单并且返回树状结构
     * 拥有sys:menu:list权限时才可以获取
     * @return Result
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result list() {
        return Result.succeed(menuService.tree());
    }

    /**
     * 新增一个菜单
     * 拥有sys:menu:save权限时才可以保存
     * @param menu 菜单实体
     * @return Result
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:menu:save')")
    public Result save(@Validated @RequestBody Menu menu) {
        menu.setCreated(LocalDateTime.now());
        menuService.save(menu);
        return Result.succeed(menu);
    }

    /**
     * 更新一个菜单
     * 拥有sys:menu:update权限时才可以更新
     * @param menu 菜单实体
     * @return Result
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:menu:update')")
    public Result update(@Validated @RequestBody Menu menu) {
        menu.setUpdated(LocalDateTime.now());
        menuService.updateById(menu);
        // 清除所有与该菜单的权限缓存
        userService.clearUserAuthorityInfoByMenuId(menu.getId());
        return Result.succeed(menu);
    }

    /**
     * 删除一个菜单
     * 拥有sys:menu:delete权限时才可以删除
     * @param id 菜单Id
     * @return Result
     */
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    public Result delete(@PathVariable(name = "id") Long id) {

        Integer count = menuService.count(id);
        if (count > 0){
            return Result.error("请先删除子菜单");
        }
        // 清除所有与该菜单的权限缓存
        userService.clearUserAuthorityInfoByMenuId(id);
        menuService.removeById(id);
        // 删除中间关联表信息
        roleMenuService.remove(new QueryWrapper<RoleMenu>().eq("menu_id",id));
        return Result.succeed("删除成功");
    }
}
