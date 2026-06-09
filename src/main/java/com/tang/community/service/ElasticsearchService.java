package com.tang.community.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import com.tang.community.dao.elasticsearch.DiscussPostRepository;
import com.tang.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectName: community
 * Package: com.tang.community.service
 * ClassName: ElasticsearchService
 * Author: tmj
 * Date: 2026/6/9 12:59
 * version: 1.0
 * Description:
 */
@Service
public class ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public void saveDiscussPost(DiscussPost discussPost) {
        discussPostRepository.save(discussPost);
    }

    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(String.valueOf(id));
    }

    public Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit) {
        // 1. 构建查询 (替代旧的 NativeSearchQueryBuilder)
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(m -> m
                        .query(keyword)
                        .fields("title", "content")
                ))
                .withSort(s -> s.field(f -> f.field("type").order(SortOrder.Desc)))
                .withSort(s -> s.field(f -> f.field("score").order(SortOrder.Desc)))
                .withSort(s -> s.field(f -> f.field("createTime").order(SortOrder.Desc)))
                .withPageable(PageRequest.of(current, limit))
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
        return page;
    }
}
