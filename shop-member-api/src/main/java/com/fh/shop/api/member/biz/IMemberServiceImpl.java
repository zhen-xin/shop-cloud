package com.fh.shop.api.member.biz;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fh.common.Constants;
import com.fh.common.ResponseEnum;
import com.fh.common.ServerResponse;
import com.fh.shop.api.member.mapper.IMemberMapper;
import com.fh.shop.api.member.po.Member;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.util.KeyUtil;
import com.fh.util.Md5Util;
import com.fh.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service("memberService")
@Transactional(rollbackFor = Exception.class)
public class IMemberServiceImpl implements IMemberService {

    @Autowired
    private IMemberMapper memberMapper;

    @Override
    public ServerResponse login(String memberName, String password) {
        //判断会员名和密码是否为空
        if(StringUtils.isEmpty(memberName) || StringUtils.isEmpty(password)){
            return ServerResponse.error(ResponseEnum.MEMBER_LOGIN_IS_NULL);
        }

        //判断用户是否存在
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("memberName",memberName);
        Member member = memberMapper.selectOne(queryWrapper);
        if (member == null){
            return ServerResponse.error(ResponseEnum.MEMBER_LOGIN_NOT_EXIST);
        }

        //判断密码是否正确
        if (!Md5Util.md5(password).equals(member.getPassword())){
            return ServerResponse.error(ResponseEnum.PASSWORD_IS_ERROR);
        }

        //判断该账号是否已经激活
        if (member.getActivate() == Constants.MEMBER_ACTIVATE_ERROR){
            Map<String,String> map = new HashMap<>();
            map.put("mail",member.getMail());
            map.put("id",member.getId()+"");
            return ServerResponse.error(ResponseEnum.MEMBER_NOT_ACTIVATE,map);
        }

        //生成签名
        //将用户信息转换为json格式
        MemberVo memberVo = new MemberVo();
        memberVo.setId(member.getId());
        memberVo.setMemberName(member.getMemberName());
        memberVo.setNickName(member.getNickName());
        String memberVoJson = JSON.toJSONString(memberVo);
        //将json格式的用户信息+秘钥生成签名
        String sign = Md5Util.md5(memberVoJson + Constants.SECRET);

        //将用户信息和签名响应给前台 [用户信息中不能包含密码]
        //将用户信息进行base64编码
        String memberVoJsonBase64 = Base64.getEncoder().encodeToString(memberVoJson.getBytes());
        String signBase64 = Base64.getEncoder().encodeToString(sign.getBytes());
        //将信息存入redis中
        RedisUtil.setex(KeyUtil.buildMemberKey(+member.getId()),"",Constants.TOKEN_EXPIRE);
        //把用户信息和签名中间用[.]隔开 x.y
        return ServerResponse.success(memberVoJsonBase64+"."+signBase64);
    }

}
