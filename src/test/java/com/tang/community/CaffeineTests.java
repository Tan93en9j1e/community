package com.tang.community;

import com.tang.community.entity.DiscussPost;
import com.tang.community.service.DiscussPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

/**
 * ProjectName: community
 * Package: com.tang.community
 * ClassName: CaffeineTests
 * Author: tmj
 * Date: 2026/6/17 15:04
 * version: 1.0
 * Description:
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CaffeineTests {
    @Autowired
    private DiscussPostService PostService;

    @Test
    public void initDataForTest() {
        for (int i = 0; i < 300000; i++) {
            DiscussPost post = new DiscussPost();
            post.setUserId(111);
            post.setTitle("互联网求职暖春计划");
            post.setContent("今年的就业形势，确实不容乐观。过了个年，仿佛跳水一般，整个讨论区哀鸿遍野！19届真的没人要吗？！18届被优化真的没有出路吗？！大家的“哀嚎”与“悲惨遭遇”牵动了每日潜伏于讨论区的牛客小哥哥");
            post.setCreateTime(new Date());
            post.setScore(Math.random() * 2000);
            PostService.addDiscussPost(post);
        }
    }

    @Test
    public void testCache() {
        System.out.println(PostService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(PostService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(PostService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(PostService.findDiscussPosts(0, 0, 10, 0));
    }
}
