package com.naown.security;

import cn.hutool.json.JSONUtil;
import com.naown.common.lang.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Token访问失败处理器
 * 访问的资源如果权限不足则会进入此方法
 * @author: chenjian
 * @since: 2021/5/16 22:30 周日
 **/
@Component
public class TokenAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json; charset=UTF-8");
        // 403权限不足
        httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

        try(ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
            outputStream.write(JSONUtil.toJsonStr(Result.error(e.getMessage())).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
    }
}
