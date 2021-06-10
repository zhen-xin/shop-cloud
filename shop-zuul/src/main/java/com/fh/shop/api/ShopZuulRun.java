package com.fh.shop.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class ShopZuulRun {

    public static void main(String[] args) {
        System.out.println("更新");
        System.out.println("dev上传");
        SpringApplication.run(ShopZuulRun.class, args);
    }

}
