package com.henu.henuxin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.henu.henuxin.mapper")
@ComponentScan(basePackages = {"com.henu"})
public class HenuxinApplication {

    public static void main(String[] args) {
        SpringApplication.run(HenuxinApplication.class, args);
    }

}
