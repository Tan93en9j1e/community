package com.tang.community.actuator;

import com.tang.community.service.DataService;
import com.tang.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * ProjectName: community
 * Package: com.tang.community.actuator
 * ClassName: DatabaseEndpoint
 * Author: tmj
 * Date: 2026/6/17 16:58
 * version: 1.0
 * Description:
 */
@Component
@Endpoint(id = "database")
public class DatabaseEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseEndpoint.class);

    @Autowired
    private DataSource dataSource;

    @ReadOperation
    public String checkConnection() {
        try (
                Connection connection = dataSource.getConnection();
        ) {
            return CommunityUtil.getJsonString(0, "连接成功");

        } catch (Exception e) {
            logger.error("检查数据库连接失败：" + e.getMessage());
            return CommunityUtil.getJsonString(1, "连接失败");
        }
    }
}
