package com.tang.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ProjectName: community
 * Package: com.tang.community.controller
 * ClassName: AlphaController
 * Author: tmj
 * Date: 2026/5/18 18:39
 * version: 1.0
 * Description:
 */
@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot";
    }
}
