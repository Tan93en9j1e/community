package com.tang.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * ProjectName: community
 * Package: com.tang.community.config
 * ClassName: AlphaConfig
 * Author: tmj
 * Date: 2026/5/18 19:29
 * version: 1.0
 * Description:
 */
@Configuration
public class AlphaConfig {
    @Bean
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
