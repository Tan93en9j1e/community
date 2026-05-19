package com.tang.community;

import com.tang.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * ProjectName: community
 * Package: com.tang.community
 * ClassName: MailTests
 * Author: tmj
 * Date: 2026/5/19 14:04
 * version: 1.0
 * Description:
 */
@SpringBootTest
@ContextConfiguration (classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail() {
        mailClient.sendMail("3124894818@qq.com", "TEST", "Welcome!");
    }

    @Test
    public void testHtmlMail() {
        Context context = new Context();
        context.setVariable("username", "Tang");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("3124894818@qq.com", "HTML", content);
    }

}
