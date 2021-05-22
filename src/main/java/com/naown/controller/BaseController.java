package com.naown.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.naown.service.*;
import com.naown.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: chenjian
 * @since: 2021/5/20 23:02 周四
 **/
@Component
public class BaseController {

    @Autowired
    public HttpServletRequest request;
    @Autowired
    public RedisUtil redisUtil;
    @Autowired
    public IUserService userService;
    @Autowired
    public IRoleService roleService;
    @Autowired
    public IMenuService menuService;
    @Autowired
    public IRoleMenuService roleMenuService;
    @Autowired
    public IUserRoleService userRoleService;

    /**
     * 获取页码
     * @return Page
     */
    public Page getPage(){
        int current = ServletRequestUtils.getIntParameter(request,"current",1);
        int size = ServletRequestUtils.getIntParameter(request,"size",10);
        return new Page(current,size);
    }
}
