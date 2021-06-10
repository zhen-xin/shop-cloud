package com.fh.shop.api.member.controller;

import com.fh.common.Constants;
import com.fh.common.ServerResponse;
import com.fh.shop.api.BaseController;
import com.fh.shop.api.member.biz.IMemberService;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.util.KeyUtil;
import com.fh.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
@Api(tags = "商品会员api")
public class MemberController extends BaseController {

    @Resource(name = "memberService")
    private IMemberService memberService;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("login")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberName", value = "会员名", dataType = "java.lang.String", required = true),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "java.lang.String", required = true)
    })
    public ServerResponse login(String memberName, String password){
        return memberService.login(memberName,password);
    }

    @GetMapping("findRequestMember")
    public ServerResponse findRequestMember(){
        MemberVo memberVo = buildMemberVo(request);
        String totalCount = RedisUtil.hget(KeyUtil.buildCartKey(memberVo.getId().toString()),Constants.CART_COUNT_FIELD);
        if(StringUtils.isEmpty(totalCount)){
            totalCount = "0";
        }
        Map<String,Object> map = new HashMap<>();
        map.put("memberVo",memberVo);
        map.put("totalCount",totalCount);
        return ServerResponse.success(map);
    }

    @GetMapping("outLogin")
    public ServerResponse loginOut(){
        MemberVo memberVo = buildMemberVo(request);
        RedisUtil.del(KeyUtil.buildMemberKey(memberVo.getId()));
        return ServerResponse.success();
    }

}
