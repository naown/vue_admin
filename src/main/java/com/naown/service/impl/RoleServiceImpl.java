package com.naown.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.naown.entity.Role;
import com.naown.mapper.RoleMapper;
import com.naown.service.IRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chenjian
 * @since 2021-05-09
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper,Role> implements IRoleService {

    @Override
    public List<Role> listRoleById(Long userId) {
        // 多表子查询？ 待试验
        return this.list(new QueryWrapper<Role>().inSql("id", "select role_id from sys_user_role where user_id =" + userId));
    }

    @Override
    public Page listPageRoleByName(Page page, String name) {
        return this.page(page,new QueryWrapper<Role>().like(StrUtil.isNotBlank(name),"name",name));
    }

    @Override
    public List<Role> listRolesByUserId(Long userId) {
        return this.list(new QueryWrapper<Role>().inSql("id","select role_id from sys_user_role where user_id = " + userId));
    }

}
