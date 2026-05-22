package com.tang.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ProjectName: community
 * Package: com.tang.community.annotation
 * ClassName: LoginRequired
 * Author: tmj
 * Date: 2026/5/22 16:34
 * version: 1.0
 * Description:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {

}
