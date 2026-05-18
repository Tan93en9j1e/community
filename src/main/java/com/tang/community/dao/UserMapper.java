package com.tang.community.dao;

import com.tang.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * ProjectName: community
 * Package: com.tang.community.dao
 * ClassName: UserMapper
 * Author: tmj
 * Date: 2026/5/18 22:31
 * version: 1.0
 * Description:
 */
@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}
