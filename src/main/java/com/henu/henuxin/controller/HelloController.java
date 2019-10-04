package com.henu.henuxin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: F
 * @Date: 2019/9/28 19:21
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(){

        return "hello netty!";
    }
}
