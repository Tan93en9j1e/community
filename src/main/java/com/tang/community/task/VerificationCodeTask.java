package com.tang.community.task;

import com.tang.community.dao.VerificationCodeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ProjectName: community
 * Package: com.tang.community.task
 * ClassName: VerificationCodeTask
 * Author: tmj
 * Date: 2026/5/22 17:53
 * version: 1.0
 * Description:
 */
@Component
public class VerificationCodeTask {
    private static final Logger logger = LoggerFactory.getLogger(VerificationCodeTask.class);

    @Autowired
    private VerificationCodeMapper verificationCodeMapper;

    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void cleanExpiredCodes() {
        int count = verificationCodeMapper.deleteExpiredCodes();
        logger.info("定期清理过期验证码，清理了" + count + "条数据");
    }
}
