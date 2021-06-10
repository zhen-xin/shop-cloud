package com.fh.shop.api.cate.controller;

import com.fh.common.ServerResponse;
import com.fh.shop.api.cate.biz.ICateService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("/api/cate")
@RestController
@Api(tags = "商品类型api")
public class CateController {

    @Resource(name = "cateService")
    private ICateService cateService;

    @RequestMapping(value = "/query",method = RequestMethod.GET)
    public ServerResponse queryCateList(){
        return cateService.queryCateList();
    }

}
