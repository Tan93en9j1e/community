package com.tang.community.dao.elasticsearch;

import com.tang.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * ProjectName: community
 * Package: com.tang.community.dao.elasticsearch
 * ClassName: DiscussPostRepository
 * Author: tmj
 * Date: 2026/6/6 16:59
 * version: 1.0
 * Description:
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, String> {

}
