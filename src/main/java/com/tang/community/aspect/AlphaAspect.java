package com.tang.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * ProjectName: community
 * Package: com.tang.community.aspect
 * ClassName: AlphaAspect
 * Author: tmj
 * Date: 2026/5/26 20:10
 * version: 1.0
 * Description:
 */
//@Component
//@Aspect
public class AlphaAspect {
    @Pointcut("execution(* com.tang.community.service.*.*(..))")
    public void pointcut() {

    }

    @Before("pointcut()")
    public void before() {
        System.out.println("before");
    }

    @After("pointcut()")
    public void after() {
        System.out.println("after");
    }

    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("afterReturning");
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
        Object object = joinPoint.proceed();
        System.out.println("around after");
        return object;
    }
}
