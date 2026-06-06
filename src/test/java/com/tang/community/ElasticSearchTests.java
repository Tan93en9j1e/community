package com.tang.community;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.tang.community.dao.DiscussPostMapper;
import com.tang.community.dao.elasticsearch.DiscussPostRepository;
import com.tang.community.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.test.context.ContextConfiguration;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * ProjectName: community
 * Package: com.tang.community
 * ClassName: ElasticSearchTests
 * Author: tmj
 * Date: 2026/6/6 17:00
 * version: 1.0
 * Description:
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticSearchTests {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void testInsert() {
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertList() {
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134, 0, 100));
    }

    @Test
    public void testUpdate() {
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("我是新人，快来和我一起玩");
        discussPostRepository.save(post);
    }

    @Test
    public void testDelete() {
//         discussPostRepository.deleteById(231);
        discussPostRepository.deleteAll();
    }

    @Test
    public void testSearchByRepository() {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(m -> m
                        .query("互联网寒冬")
                        .fields("title", "content")
                ))
                .withSort(s -> s.field(f -> f.field("type").order(SortOrder.Desc)))
                .withSort(s -> s.field(f -> f.field("score").order(SortOrder.Desc)))
                .withSort(s -> s.field(f -> f.field("createTime").order(SortOrder.Desc)))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightQuery(
                        new HighlightQuery(
                                new Highlight(List.of(
                                        new HighlightField("title"),
                                        new HighlightField("content")
                                )),
                                DiscussPost.class
                        )
                )
                .build();

        // 2. 执行查询（用 elasticsearchTemplate，而非 repository.search()）
        SearchHits<DiscussPost> searchHits = elasticsearchTemplate.search(query, DiscussPost.class);

        // 3. 处理结果 (替代旧的 SearchResultMapper 中繁琐的手动映射)
        List<DiscussPost> list = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : searchHits) {
            // 【核心变化】：框架已经自动把 _source 映射到了实体类中，直接 getContent() 即可！
            DiscussPost post = hit.getContent();
            list.add(post);
        }

        // 4. 手动封装为 Page 对象 (完美平替旧版的 AggregatedPageImpl)
        Page<DiscussPost> page = new PageImpl<>(list, query.getPageable(), searchHits.getTotalHits());

        // 5. 打印结果
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }

    @Test
    public void testSearchByTemplate() {
        // 1. 构建查询 (替代旧的 NativeSearchQueryBuilder)
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(m -> m
                        .query("互联网寒冬")
                        .fields("title", "content")
                ))
                .withSort(s -> s.field(f -> f.field("type").order(SortOrder.Desc)))
                .withSort(s -> s.field(f -> f.field("score").order(SortOrder.Desc)))
                .withSort(s -> s.field(f -> f.field("createTime").order(SortOrder.Desc)))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightQuery(
                        new HighlightQuery(
                                new Highlight(List.of(
                                        new HighlightField("title"),
                                        new HighlightField("content")
                                )),
                                DiscussPost.class
                        )
                )
                .build();

        // 2. 执行查询 (替代旧的 queryForPage)
        SearchHits<DiscussPost> searchHits = elasticsearchTemplate.search(query, DiscussPost.class);

        // 3. 处理结果 (替代旧的 SearchResultMapper 中繁琐的手动映射)
        List<DiscussPost> list = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : searchHits) {
            // 【核心变化】：框架已经自动把 _source 映射到了实体类中，直接 getContent() 即可！
            DiscussPost post = hit.getContent();

            // 处理高亮显示的结果 (覆盖原字段)
            List<String> titleHighlights = hit.getHighlightField("title");
            if (titleHighlights != null && !titleHighlights.isEmpty()) {
                post.setTitle(titleHighlights.get(0));
            }

            List<String> contentHighlights = hit.getHighlightField("content");
            if (contentHighlights != null && !contentHighlights.isEmpty()) {
                post.setContent(contentHighlights.get(0));
            }

            list.add(post);
        }

        // 4. 手动封装为 Page 对象 (完美平替旧版的 AggregatedPageImpl)
        Page<DiscussPost> page = new PageImpl<>(list, query.getPageable(), searchHits.getTotalHits());

        // 5. 打印结果
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }
}
