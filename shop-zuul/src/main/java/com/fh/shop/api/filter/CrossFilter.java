package com.fh.shop.api.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Component
public class CrossFilter extends ZuulFilter {
    @Override
    public String filterType() {
        //在路由之前执行
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletResponse response = currentContext.getResponse();
        //处理跨域问题h
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        //处理客户端传过来的自定义头信息
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "token,content-type,x-token");
        //处理特殊的请求方式 delete,put,get,post
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "DELETE,PUT,GET,POST");
        return null;
    }
}
