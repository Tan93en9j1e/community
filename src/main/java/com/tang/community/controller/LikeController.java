package com.tang.community.controller;

import com.tang.community.annotation.LoginRequired;
import com.tang.community.entity.User;
import com.tang.community.service.LikeService;
import com.tang.community.util.CommunityUtil;
import com.tang.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * ProjectName: community
 * Package: com.tang.community.controller
 * ClassName: LikeController
 * Author: tmj
 * Date: 2026/5/27 15:22
 * version: 1.0
 * Description:
 */
@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String like(int entityId, int entityType,int entityUserId) {
        User user = hostHolder.getUser();

        // 点赞
        likeService.like(user.getId(), entityType, entityId,entityUserId);
        // 统计数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 查询当前用户是否点赞
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        return CommunityUtil.getJsonString(0, null, map);
    }
}
