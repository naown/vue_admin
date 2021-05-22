package com.naown.security;

import com.naown.common.exception.CaptchaException;
import com.naown.common.lang.Const;
import com.naown.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO redis中没有验证码时则会报用户名密码错误
 * 自定义验证码拦截器
 * @author: chenjian
 * @since: 2021/5/13 22:38 周四
 **/
@Component
public class CaptchaFilter extends OncePerRequestFilter {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String url = httpServletRequest.getRequestURI();
        if ("/login".equals(url) && "POST".equals(httpServletRequest.getMethod())){
            // 校验验证码
            try {
                validate(httpServletRequest);
            }catch (CaptchaException e){
                // 如果不正确,就调整到认证失败处理器
                loginFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private synchronized void validate(HttpServletRequest httpServletRequest) throws CaptchaException {
        String code = httpServletRequest.getParameter("code");
        String key = httpServletRequest.getParameter("key");
        if (StringUtils.isBlank(code) || StringUtils.isBlank(key)){
            throw new CaptchaException("验证码不能为空");
        }
        if (!code.equals(redisUtil.hget(Const.CAPTCHA_KEY,key))){
            throw new CaptchaException("验证码错误");
        }
        redisUtil.hdel(Const.CAPTCHA_KEY,key);
    }
}
