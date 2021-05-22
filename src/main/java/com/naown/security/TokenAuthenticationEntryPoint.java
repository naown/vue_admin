package com.naown.security;

import cn.hutool.json.JSONUtil;
import com.naown.common.lang.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Token未认证处理器
 * 如果访问的资源需要登录后才能访问会进入到此方法
 * @author: chenjian
 * @since: 2021/5/16 22:29 周日
 **/
@Component
public class TokenAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json; charset=UTF-8");
        // 401 未进行认证
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        try(ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
            outputStream.write(JSONUtil.toJsonStr(Result.error("请先登录")).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
    }
}
