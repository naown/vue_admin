package com.naown.security;

import cn.hutool.json.JSONUtil;
import com.naown.common.lang.Result;
import com.naown.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author: chenjian
 * @since: 2021/5/18 22:38 周二
 **/
@Component
public class LogoutHandler implements LogoutSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {

        if (authentication != null){
            new SecurityContextLogoutHandler().logout(httpServletRequest,httpServletResponse,authentication);
        }
        httpServletResponse.setContentType("application/json; charset=UTF-8");

        try(ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
            httpServletResponse.setHeader(jwtUtils.getHeader(),"");
            outputStream.write(JSONUtil.toJsonStr(Result.succeed("")).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
    }
}
