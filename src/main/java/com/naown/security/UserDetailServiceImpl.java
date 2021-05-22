package com.naown.security;

import com.naown.entity.User;
import com.naown.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: chenjian
 * @since: 2021/5/16 22:43 周日
 **/
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByUserName(username);
        if (null == user){
            throw new UsernameNotFoundException("用户名或密码不正确");
        }
        // TODO 后续使用MapStruct赋值
        return new Account(user.getId(),user.getUsername(),user.getPassword(),getUserAuthority(user.getId()));
    }

    /**
     * 获取用户权限信息(角色、菜单权限)
     * @param userId
     * @return
     */
    public List<GrantedAuthority> getUserAuthority(Long userId){
        // 角色(ROLE_admin)、菜单操作权限sys:user:list
        // ROLE_admin,sys:user:list....
        String authority = userService.getUserAuthorityInfo(userId);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authority);
    }
}
