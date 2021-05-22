package com.naown.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * 自定义用户详情类
 * @author: chenjian
 * @since: 2021/5/16 22:53 周日
 **/
public class Account implements UserDetails {

    private Long userId;

    private String password;

    private String username;

    private Collection<? extends GrantedAuthority> authorities;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private boolean enabled;

    public Account(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this(userId,username, password, true, true, true, true, authorities);
    }

    public Account(Long userId,String username, String password, boolean enabled, boolean accountNonExpired,
                boolean credentialsNonExpired, boolean accountNonLocked,
                Collection<? extends GrantedAuthority> authorities) {
        Assert.isTrue(username != null && !"".equals(username) && password != null,
                "Cannot pass null or empty values to constructor");
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
