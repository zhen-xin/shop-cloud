package com.fh.shop.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ShopCartApiRun {

    public static void main(String[] args) {
        SpringApplication.run(ShopCartApiRun.class,args);
    }

}
