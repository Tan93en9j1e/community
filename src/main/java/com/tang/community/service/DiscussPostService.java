package com.tang.community.service;

import com.tang.community.entity.DiscussPost;
import com.tang.community.dao.DiscussPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ProjectName: community
 * Package: com.tang.community.service
 * ClassName: DiscussPostService
 * Author: tmj
 * Date: 2026/5/19 08:29
 * version: 1.0
 * Description:
 */
@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}
