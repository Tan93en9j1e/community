package com.tang.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * ProjectName: community
 * Package: com.tang.community.dao
 * ClassName: AlphaDaoMybatisImpl
 * Author: tmj
 * Date: 2026/5/18 19:16
 * version: 1.0
 * Description:
 */
@Repository
@Primary
public class AlphaDaoMybatisImpl implements AlphaDao{

    @Override
    public String select() {
        return "Mybatis";
    }
}
