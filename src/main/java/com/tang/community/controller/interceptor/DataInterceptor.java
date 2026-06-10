package com.tang.community.controller.interceptor;

import com.tang.community.entity.User;
import com.tang.community.service.DataService;
import com.tang.community.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * ProjectName: community
 * Package: com.tang.community.controller.interceptor
 * ClassName: DataInterceptor
 * Author: tmj
 * Date: 2026/6/10 21:24
 * version: 1.0
 * Description:
 */
@Component
public class DataInterceptor implements HandlerInterceptor {
    @Autowired
    private DataService dataService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录UV
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);
        // 记录DAU
        User user = hostHolder.getUser();
        if (user != null) {
            dataService.recordDAU(user.getId());
        }
        return true;
    }
}
