package com.tang.community.controller.interceptor;

import com.tang.community.config.SecurityConfig;
import com.tang.community.entity.LoginTicket;
import com.tang.community.entity.User;
import com.tang.community.service.UserService;
import com.tang.community.util.CookieUtil;
import com.tang.community.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;
import java.util.Date;

/**
 * ProjectName: community
 * Package: com.tang.community.controller.interceptor
 * ClassName: LoginTicketInterceptor
 * Author: tmj
 * Date: 2026/5/21 09:23
 * version: 1.0
 * Description:
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getValue(request, "ticket");

        // ⚠️ 核心原则：只有 ticket 完全有效时才设置认证信息，否则什么都不做！
        if (ticket != null) {
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            if (loginTicket != null && loginTicket.getStatus() == 0
                    && loginTicket.getExpired().after(new Date())) {

                User user = userService.findUserById(loginTicket.getUserId());
                if (user != null) {
                    // 1. 设置业务层的 HostHolder
                    hostHolder.setUser(user);

                    // 2. 创建并设置 Security 认证对象
                    Collection<? extends GrantedAuthority> authorities = userService.getAuthorities(user.getId());
                    Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // ✅ 3. 【关键修复】将 SecurityContext 手动同步到 Session 中
                    request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
                }
            }
        }
        // 如果 ticket 为 null 或无效，直接跳过，不要设置 AnonymousAuthenticationToken！
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (modelAndView != null && user != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 仅清理业务层的 HostHolder，不要手动清除 SecurityContext
        hostHolder.clear();
    }
}
