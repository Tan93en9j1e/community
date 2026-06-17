package com.tang.community.config;

import com.tang.community.util.CommunityConstant;
import com.tang.community.util.CommunityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * ProjectName: community
 * Package: com.tang.community.config
 * ClassName: SecurityConfig
 * Author: tmj
 * Date: 2026/6/10 10:44
 * version: 1.0
 * Description:
 */
//@Configuration
//public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {
//
//    @Override
//    public void configure(WebSecurity web) throws Exception{
//        web.ignoring().antMatchers("/resources/**");
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        // 授权
//        http.authorizeHttpRequests()
//                .antMatchers(
//                        "/user/setting",
//                        "/user/upload",
//                        "/discuss/add",
//                        "/comment/add/**",
//                        "/letter/**",
//                        "/notice/**",
//                        "/like",
//                        "/follow",
//                        "/unfollow"
//                )
//                .hasAnyAuthority(
//                        AUTHORITY_USER,
//                        AUTHORITY_ADMIN,
//                        AUTHORITY_MODERATOR
//                ).antMatchers(
//                         "/discuss/top",
//                          "/discuss/wonderful"
//                ).hasAnyAuthority(AUTHORITY_MODERATOR)
//                .antMatchers("/discuss/delete")
//                .hasAnyAuthority(AUTHORITY_ADMIN)
//                .anyRequest().permitAll();
//
//        //权限不够时的处理
//        http.exceptionHandling()
//                .authenticationEntryPoint(new AuthenticationEntryPoint() {
//                    //没有登录
//                    @Override
//                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
//                        String xRequestedWith = request.getHeader("x-requested-with");
//                        if ("XMLHttpRequest".equals(xRequestedWith)) {
//                            response.setContentType("application/plain;charset=utf-8");
//                            PrintWriter writer = response.getWriter();
//                            writer.write(CommunityUtil.getJsonString(403, "未登录"));
//                        } else {
//                            response.sendRedirect(request.getContextPath() + "/login");
//                        }
//                    }
//                }).accessDeniedHandler(new AccessDeniedHandler() {
//                    //权限不足
//                    @Override
//                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
//                        String xRequestedWith = request.getHeader("x-requested-with");
//                        if ("XMLHttpRequest".equals(xRequestedWith)) {
//                            response.setContentType("application/plain;charset=utf-8");
//                            PrintWriter writer = response.getWriter();
//                            writer.write(CommunityUtil.getJsonString(403, "无权限"));
//                        } else {
//                            response.sendRedirect(request.getContextPath() + "/denied");
//                        }
//                    }
//                });
//
//        //Security底层默认会拦截/logout请求，进行退出处理。
//        //覆盖它默认的逻辑，才能执行我们自己的退出代码
//        http.logout().logoutUrl("/securitylogout");
//    }
//}

@Configuration
@EnableWebSecurity
public class SecurityConfig implements CommunityConstant {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/captcha").permitAll()
                .requestMatchers("/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                )
                .hasAnyAuthority(AUTHORITY_USER, AUTHORITY_ADMIN, AUTHORITY_MODERATOR)
                .requestMatchers("/discuss/top", "/discuss/wonderful")
                .hasAnyAuthority(AUTHORITY_MODERATOR)
                .requestMatchers(
                        "/discuss/delete",
                        "/data/**",
                        "/actuator/**"
                )
                .hasAnyAuthority(AUTHORITY_ADMIN)
                .anyRequest().permitAll()
        );

        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    String xRequestedWith = request.getHeader("x-requested-with");
                    if ("XMLHttpRequest".equals(xRequestedWith)) {
                        response.setContentType("application/json;charset=utf-8");
                        response.getWriter().write(CommunityUtil.getJsonString(401, "请先登录"));
                    } else {
                        response.sendRedirect(request.getContextPath() + "/login");
                    }
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    String xRequestedWith = request.getHeader("x-requested-with");
                    if ("XMLHttpRequest".equals(xRequestedWith)) {
                        response.setContentType("application/json;charset=utf-8");
                        response.getWriter().write(CommunityUtil.getJsonString(403, "权限不足"));
                    } else {
                        response.sendRedirect(request.getContextPath() + "/denied");
                    }
                })
        );

        http.logout(logout -> logout
                .logoutUrl("/securitylogout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutSuccessUrl("/login")
        );

        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}