package com.tang.community.util;

/**
 * ProjectName: community
 * Package: com.tang.community.util
 * ClassName: CommunityConstant
 * Author: tmj
 * Date: 2026/5/19 17:01
 * version: 1.0
 * Description:
 */
public interface CommunityConstant {
    //激活成功
    int ACTIVATION_SUCCESS = 0;
    //重复激活
    int ACTIVATION_REPEAT = 1;
    //激活失败
    int ACTIVATION_FAILURE = 2;
    //默认状态的登录凭证超时时间
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    //记住状态的登录凭证超时时间
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    int VERIFICATION_CODE_UNUSED = 0;
    int VERIFICATION_CODE_USED = 1;
    int VERIFICATION_CODE_EXPIRED = 2;
    int VERIFICATION_CODE_EXPIRED_SECONDS = 300;

    //实体类型 帖子
    int ENTITY_TYPE_POST = 1;
    //实体类型 评论
    int ENTITY_TYPE_COMMENT = 2;
    //实体类型 用户
    int ENTITY_TYPE_USER = 3;

    String TOPIC_COMMENT = "comment";
    String TOPIC_LIKE = "like";
    String TOPIC_FOLLOW = "follow";
    String TOPIC_PUBLISH = "publish";
    String TOPIC_DELETE = "delete";
    String TOPIC_SHARE = "share";

    int SYSTEM_USER_ID = 1;

    String AUTHORITY_USER = "user";
    String AUTHORITY_ADMIN = "admin";
    String AUTHORITY_MODERATOR = "moderator";


}
