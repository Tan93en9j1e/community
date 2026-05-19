package com.tang.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * ProjectName: community
 * Package: com.tang.community.util
 * ClassName: CommunityUtil
 * Author: tmj
 * Date: 2026/5/19 15:16
 * version: 1.0
 * Description:
 */
public class CommunityUtil {
    //生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    //MD5加密
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
