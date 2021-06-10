package com.fh.shop.api;

import com.alibaba.fastjson.JSON;
import com.fh.common.Constants;
import com.fh.shop.api.member.vo.MemberVo;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class BaseController {

    public MemberVo buildMemberVo(HttpServletRequest request){
        try {
            String memberVoJson = request.getHeader(Constants.REQUEST_MEMBER);
            String memberJson = URLDecoder.decode(memberVoJson, "utf-8");
            return JSON.parseObject(memberJson, MemberVo.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
