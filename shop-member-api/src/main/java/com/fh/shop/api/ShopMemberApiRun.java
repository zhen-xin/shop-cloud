package com.fh.shop.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fh.shop.api.member.mapper")
public class ShopMemberApiRun {

    public static void main(String[] args) {
        SpringApplication.run(ShopMemberApiRun.class, args);
    }

}
