package com.tang.community.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * ProjectName: community
 * Package: com.tang.community.quartz
 * ClassName: AlphaJob
 * Author: tmj
 * Date: 2026/6/10 22:31
 * version: 1.0
 * Description:
 */

public class AlphaJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println(Thread.currentThread().getName() + ": execute a quartz job");
    }
}
