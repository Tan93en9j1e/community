package com.tang.community.util;

/**
 * ProjectName: community
 * Package: com.tang.community.util
 * ClassName: RedisKeyUtil
 * Author: tmj
 * Date: 2026/5/27 15:14
 * version: 1.0
 * Description:
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    //某个实体的赞
    //like:entity:entityType:entityId ->set(userId)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }
}
