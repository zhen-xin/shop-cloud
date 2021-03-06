package com.fh.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EurekaServerRun {

    public static void main(String[] args) {
        System.out.println("拉取后更改");
        SpringApplication.run(EurekaServerRun.class, args);
    }

}
