package com.tang.community.dao;

import com.tang.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * ProjectName: community
 * Package: com.tang.community.dao
 * ClassName: CommentMapper
 * Author: tmj
 * Date: 2026/5/25 09:57
 * version: 1.0
 * Description:
 */
@Mapper
public interface CommentMapper {
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

}
