package com.tang.community;

import com.tang.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * ProjectName: community
 * Package: com.tang.community
 * ClassName: Transaction
 * Author: tmj
 * Date: 2026/5/24 16:49
 * version: 1.0
 * Description:
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTests {

    @Autowired
    private AlphaService alphaService;

    @Test
    public void testSave1() {
        Object obj = alphaService.save1();
        System.out.println(obj);
    }
    @Test
    public void testSave2() {
        Object obj = alphaService.save2();
        System.out.println(obj);
    }
}
