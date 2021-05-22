package com.naown.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.naown.common.dto.PasswordDTO;
import com.naown.common.lang.Const;
import com.naown.common.lang.Result;
import com.naown.entity.Role;
import com.naown.entity.User;
import com.naown.entity.UserRole;
import com.naown.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author chenjian
 * @since 2021-05-09
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 暂未用到 -- 废弃
     * @param id 用户id
     * @return Result
     */
    @PreAuthorize("hasAnyAuthority('sys:user:list')")
    @GetMapping("/info/{id}")
    public Result info(@PathVariable(name = "id") Long id){
        User user = userService.getById(id);
        Assert.notNull(user,"找不到用户");
        List<Role> roles = roleService.listRolesByUserId(id);
        user.setRoles(roles);
        return Result.succeed(user);
    }

    /**
     * 获取用户列表 如果传了用户名则根据用户名查询
     * 拥有sys:user:list权限时才可以访问
     * @param username 用户名
     * @return Result
     */
    @PreAuthorize("hasAnyAuthority('sys:user:list')")
    @GetMapping("/list")
    public Result list(String username){
        Page<User> page = userService.page(getPage(), new QueryWrapper<User>().like(StrUtil.isNotBlank(username), "username", username));
        page.getRecords().forEach(user -> {
            user.setRoles(roleService.listRolesByUserId(user.getId()));
        });
        return Result.succeed(page);
    }

    /**
     * 保存用户
     * 拥有sys:user:save权限时才可以保存
     * @param user 用户实体
     * @return Result
     */
    @PreAuthorize("hasAnyAuthority('sys:user:save')")
    @PostMapping("/save")
    public Result save(@Validated @RequestBody User user){
        user.setCreated(LocalDateTime.now());
        user.setPassword(bCryptPasswordEncoder.encode(Const.DEFAULT_PASSWORD));
        if (null == user.getStatus()){
            user.setStatus(Const.STATUS_ON);
        }
        if ("".equals(user.getAvatar()) || null == user.getAvatar()){
            user.setAvatar(Const.DEFAULT_AVATAR);
        }
        userService.save(user);
        return Result.succeed(user);
    }

    /**
     * 更新用户
     * 拥有sys:user:update权限时才可以更新
     * @param user 用户实体
     * @return Result
     */
    @PreAuthorize("hasAnyAuthority('sys:user:update')")
    @PostMapping("/update")
    public Result update(@Validated @RequestBody User user){
        user.setUpdated(LocalDateTime.now());
        userService.updateById(user);
        return Result.succeed(user);
    }

    /**
     * 删除用户
     * 拥有sys:user:delete权限时才可以删除
     * @param ids 用户Id集合
     * @return Result
     */
    @PreAuthorize("hasAnyAuthority('sys:user:delete')")
    @PostMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public Result delete(@RequestBody Long[] ids){
        userService.removeByIds(Arrays.asList(ids));
        userRoleService.remove(new QueryWrapper<UserRole>().in("user_id",Arrays.asList(ids)));
        return Result.succeed("");
    }

    /**
     * 分配角色
     * 拥有sys:user:role权限时才可以分配角色
     * @param userId 用户Id
     * @param roleIds 角色Id集合
     * @return Result
     */
    @PreAuthorize("hasAnyAuthority('sys:user:role')")
    @PostMapping("/role/{userId}")
    @Transactional(rollbackFor = Exception.class)
    public Result rolePerm(@PathVariable Long userId, @RequestBody Long[] roleIds){
        List<UserRole> userRoles = new ArrayList<>();
        Arrays.stream(roleIds).forEach(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setRoleId(roleId);
            userRole.setUserId(userId);

            userRoles.add(userRole);
        });

        userRoleService.remove(new QueryWrapper<UserRole>().eq("user_id",userId));
        userRoleService.saveBatch(userRoles);

        // 删除缓存
        User user = userService.getById(userId);
        userService.clearUserAuthorityInfo(user.getUsername());
        return Result.succeed("");
    }

    /**
     * 重置密码
     * 拥有sys:user:repass权限时才可以重置密码
     * @return Result
     */
    @PreAuthorize("hasAnyAuthority('sys:user:repass')")
    @PostMapping("/repass")
    public Result repass(@RequestBody Long userId){
        User user = userService.getById(userId);
        user.setPassword(bCryptPasswordEncoder.encode(Const.DEFAULT_PASSWORD));
        user.setUpdated(LocalDateTime.now());
        userService.updateById(user);
        return Result.succeed("");
    }

    /**
     * 修改密码
     * @param passwordDTO 密码DTO
     * @param principal security
     * @return Result
     */
    @PostMapping("/updatePass")
    public Result updatePass(@Validated @RequestBody PasswordDTO passwordDTO, Principal principal){
        User user = userService.getByUserName(principal.getName());
        boolean matches = bCryptPasswordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword());
        if (!matches){
            return Result.error("旧密码不正确");
        }
        user.setUpdated(LocalDateTime.now());
        user.setPassword(bCryptPasswordEncoder.encode(passwordDTO.getNewPassword()));
        userService.updateById(user);
        return Result.succeed("");
    }
}
