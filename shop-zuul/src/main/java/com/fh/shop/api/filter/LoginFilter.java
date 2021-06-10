package com.fh.shop.api.filter;

import com.alibaba.fastjson.JSON;
import com.fh.common.Constants;
import com.fh.common.ResponseEnum;
import com.fh.common.ServerResponse;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.util.KeyUtil;
import com.fh.util.Md5Util;
import com.fh.util.RedisUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;

@Component
public class LoginFilter extends ZuulFilter {

    @Value("${shop.urlCheck}")
    private List<String> urlCheckList;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    @SneakyThrows
    public Object run() throws ZuulException{
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();

        //判断是否是预请求
        String methodAction = request.getMethod();
        if(methodAction.equalsIgnoreCase("options")){
            // 禁止路由 不会继续向微服务发送请求
            currentContext.setSendZuulResponse(false);
            return null;
        }

        //判断是否是不需要拦截就能访问的方法
        String requestURI = request.getRequestURI();
        for (String checkUrl : urlCheckList) {
            if(requestURI.contains(checkUrl)){
                return null;
            }
        }

        //验证头信息
        String header = request.getHeader("token");
        if(StringUtils.isEmpty(header)){
            return buildResponseBody(ResponseEnum.TOKEN_IS_MISS);
        }

        //判断头信息是否完整
        String[] headerArr = header.split("\\.");
        if(headerArr.length != 2){
            return buildResponseBody(ResponseEnum.TOKEN_IS_NOT_FULL);
        }

        //验签
        String memberVoJson = new String(Base64.getDecoder().decode(headerArr[0]),"utf-8");
        String sign = new String(Base64.getDecoder().decode(headerArr[1]),"utf-8");
        String newSign = Md5Util.md5(memberVoJson + Constants.SECRET);
        if (!sign.equals(newSign)){
            return buildResponseBody(ResponseEnum.TOKEN_IS_EMPTY);
        }

        //将json转换成java对象
        MemberVo memberVo = JSON.parseObject(memberVoJson, MemberVo.class);
        Long id = memberVo.getId();
        //判断是否过期
        if(!RedisUtil.exist(KeyUtil.buildMemberKey(id))){
            return buildResponseBody(ResponseEnum.LOGIN_OUT);
        }
        //续命
        RedisUtil.expire(KeyUtil.buildMemberKey(id),Constants.TOKEN_EXPIRE);

        //将用户信息存入header中，同时解决响应的乱码问题
        currentContext.addZuulRequestHeader(Constants.REQUEST_MEMBER, URLEncoder.encode(memberVoJson,"utf-8"));

        return null;
    }

    private Object buildResponseBody(ResponseEnum responseEnum) {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletResponse response = currentContext.getResponse();
        //解决中文乱码的问题
        response.setContentType("application/json;charset=utf-8");
        ServerResponse error = ServerResponse.error(responseEnum);
        currentContext.setResponseBody(JSON.toJSONString(error));
        //禁止路由转发
        currentContext.setSendZuulResponse(false);
        return null;
    }
}
