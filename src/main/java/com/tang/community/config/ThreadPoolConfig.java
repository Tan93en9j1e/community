package com.tang.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ProjectName: community
 * Package: com.tang.community.config
 * ClassName: ThreadPoolConfig
 * Author: tmj
 * Date: 2026/6/10 22:11
 * version: 1.0
 * Description:
 */
@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {
}
