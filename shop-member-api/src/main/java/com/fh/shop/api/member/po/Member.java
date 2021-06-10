package com.fh.shop.api.member.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class Member implements Serializable {
    private Long id;
    private String memberName;
    private String password;
    private String nickName;
    private String phone;
    private String mail;
    private Integer activate;
    //积分
    private Integer integral;
}
