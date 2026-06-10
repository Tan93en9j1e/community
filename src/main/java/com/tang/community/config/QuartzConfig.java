package com.tang.community.config;

import com.tang.community.quartz.AlphaJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * ProjectName: community
 * Package: com.tang.community.config
 * ClassName: QuartzConfig
 * Author: tmj
 * Date: 2026/6/10 22:32
 * version: 1.0
 * Description:配置->数据库->调用
 */
@Configuration
public class QuartzConfig {

    // FactoryBean可简化Bean的实例化过程:
    // 1. 通过FactoryBean封装Bean的实例化过程
    // 2. 将FactoryBean定义为Bean，在容器中生成FactoryBean和FactoryBean所生成的Bean
    // 3. 将FactoryBean所生成的Bean注入到其他的Bean中
    // 4. 当需要FactoryBean所生成Bean的时候，就直接获取FactoryBean所生成Bean即可

    // 配置JobDetail
//    @Bean
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    // 配置Trigger(SimpleTriggerFactoryBean,CronTriggerFactoryBean)
//    @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000);
        factoryBean.setJobDataAsMap(new JobDataMap());
        return factoryBean;
    }
}
