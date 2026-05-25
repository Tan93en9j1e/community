package com.tang.community.service;

import com.tang.community.dao.CommentMapper;
import com.tang.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ProjectName: community
 * Package: com.tang.community.service
 * ClassName: CommentService
 * Author: tmj
 * Date: 2026/5/25 10:04
 * version: 1.0
 * Description:
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }
}
