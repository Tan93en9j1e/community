package com.tang.community.dao;

import org.springframework.stereotype.Repository;

/**
 * ProjectName: community
 * Package: com.tang.community.dao
 * ClassName: AlphaDaoHibernateImpl
 * Author: tmj
 * Date: 2026/5/18 19:14
 * version: 1.0
 * Description:
 */
@Repository("alphaHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao{

    @Override
    public String select() {
        return "Hibernate";
    }
}
