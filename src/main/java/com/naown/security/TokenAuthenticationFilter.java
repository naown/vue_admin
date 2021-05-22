package com.naown.security;

import cn.hutool.core.util.StrUtil;
import com.naown.entity.User;
import com.naown.service.IUserService;
import com.naown.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义Token验证过滤器
 * @author: chenjian
 * @since: 2021/5/16 21:32 周日
 **/
public class TokenAuthenticationFilter extends BasicAuthenticationFilter {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailServiceImpl userDetailService;
    @Autowired
    private IUserService userService;

    public TokenAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader(jwtUtils.getHeader());
        if (StrUtil.isBlankOrUndefined(token)){
            chain.doFilter(request,response);
            return;
        }

        Claims claim = jwtUtils.getClaimByToken(token);
        if (null == claim){
            throw new JwtException("token 异常");
        }else if (jwtUtils.isTokenExpired(claim)){
            throw new JwtException("token 已过期");
        }

        String username = claim.getSubject();
        User user = userService.getByUserName(username);
        UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(username, null,userDetailService.getUserAuthority(user.getId()));
        SecurityContextHolder.getContext().setAuthentication(userToken);
        chain.doFilter(request,response);
    }
}
