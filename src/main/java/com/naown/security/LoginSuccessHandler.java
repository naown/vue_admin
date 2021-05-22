package com.naown.security;

import cn.hutool.json.JSONUtil;
import com.naown.common.lang.Result;
import com.naown.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 登录成功处理器
 * @author: chenjian
 * @since: 2021/5/13 19:49 周四
 **/
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json; charset=UTF-8");
        try(ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
            // 生成jwt 并且放置到请求头中
            String token = jwtUtils.generateToken(authentication.getName());
            httpServletResponse.setHeader(jwtUtils.getHeader(),token);

            Result result = Result.succeed("");
            outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
    }
}
