package com.fh.shop.api.member.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class MemberMailParam implements Serializable {
    private Long id;
    private String password;
    private String confirmPassword;
    private String code;
    private String mail;
}
