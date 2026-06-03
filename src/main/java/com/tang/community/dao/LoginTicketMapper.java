package com.tang.community.dao;

import com.tang.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ProjectName: community
 * Package: com.tang.community.dao
 * ClassName: LoginTicketMapper
 * Author: tmj
 * Date: 2026/5/20 16:43
 * version: 1.0
 * Description:
 */
@Mapper
@Deprecated
public interface LoginTicketMapper {
    @Insert({"insert into login_ticket(user_id, ticket, status, expired) values (#{userId}, #{ticket}, #{status}, #{expired})"})
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);


    @Select({"select id, user_id, ticket, status, expired from login_ticket where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);


    @Update({"update login_ticket set status=#{status} where ticket=#{ticket}"})
    int updateStatus(String ticket, int status);
}
