package com.tang.community.service;

import com.tang.community.entity.User;
import com.tang.community.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ProjectName: community
 * Package: com.tang.community.service
 * ClassName: UserService
 * Author: tmj
 * Date: 2026/5/19 08:32
 * version: 1.0
 * Description:
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }
}
