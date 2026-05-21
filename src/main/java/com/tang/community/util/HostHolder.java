package com.tang.community.util;

import com.tang.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * ProjectName: community
 * Package: com.tang.community.util
 * ClassName: HostHolder
 * Author: tmj
 * Date: 2026/5/21 17:20
 * version: 1.0
 * Description:持有用户信息，用于代替session对象
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
