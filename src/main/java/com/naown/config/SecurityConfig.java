package com.naown.config;

import com.naown.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security配置类
 * @EnableGlobalMethodSecurity 作用是启用注解形式配置权限 prePostEnabled是代表使用哪种类型的注解 prePostEnabled是在方法访问前就进行权限校验
 * @author: chenjian
 * @since: 2021/5/12 23:41 周三
 **/
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoginFailureHandler loginFailureHandler;
    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
    @Autowired
    private CaptchaFilter captchaFilter;
    @Autowired
    private TokenAuthenticationEntryPoint tokenAuthenticationEntryPoint;
    @Autowired
    private TokenAccessDeniedHandler tokenAccessDeniedHandler;
    @Autowired
    private UserDetailServiceImpl userDetailService;
    @Autowired
    private LogoutHandler logoutHandler;

    private static final String[] URL_WHITE_LIST = {
        "/login",
        "/logout",
        "/captcha",
        "/favicon.ico"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
        // 登录配置
        .formLogin()
            // 成功处理器
            .successHandler(loginSuccessHandler)
            // 失败处理器
            .failureHandler(loginFailureHandler)
        /*.and()
        TODO 可以配置退出登录逻辑
            .logout()*/
        .and()
            // 退出处理器
            .logout()
            .logoutSuccessHandler(logoutHandler)
        // 禁用session
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        // 配置拦截规则
        .and()
            .authorizeRequests()
            // 放行路由
            .antMatchers(URL_WHITE_LIST).permitAll()
            // 放行OPTIONS请求
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .anyRequest()
            .authenticated()
        // 异常处理器
        .and()
            .exceptionHandling()
            .authenticationEntryPoint(tokenAuthenticationEntryPoint)
            .accessDeniedHandler(tokenAccessDeniedHandler)
        // 配置自定义过滤器
        .and()
            // 配置自定义过滤器，过滤时机在UsernamePasswordAuthenticationFilter之前
            .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class)
            // 配置自己定义token过滤器
            .addFilter(tokenAuthenticationFilter());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
    }

    /**
     * 自定义token过滤器
     * @return
     * @throws Exception
     */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() throws Exception {
        return new TokenAuthenticationFilter(authenticationManager());
    }

    /**
     * 密码加密策略
     * @return
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
