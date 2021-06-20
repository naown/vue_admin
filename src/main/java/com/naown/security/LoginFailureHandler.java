package com.naown.security;

import cn.hutool.json.JSONUtil;
import com.naown.common.lang.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 登录失败处理器
 * @author: chenjian
 * @since: 2021/5/13 19:49 周四
 **/
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json; charset=UTF-8");

        try(ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
            outputStream.write(JSONUtil.toJsonStr(Result.error(errorMessage(e))).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
    }

    public String errorMessage(AuthenticationException e){
        if (e.getMessage().contains("验证码")){
            return e.getMessage();
        }
        return "用户名或密码不正确";
    }
}
