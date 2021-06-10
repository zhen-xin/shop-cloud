package com.fh.shop.api.goods.controller;

import com.fh.common.ServerResponse;
import com.fh.shop.api.goods.biz.ISpuService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/spu")
@Api(tags = "商品spuApi")
public class SpuController {

    @Resource(name = "spuService")
    private ISpuService spuService;

    @GetMapping("findRecommendNewProductList")
    public ServerResponse findRecommendNewProductList(){
        return spuService.findRecommendNewProductList();
    }

    @GetMapping("findSkuById")
    public ServerResponse findSkuById(@RequestParam("id") Long id){
        return spuService.findSkuById(id);
    }
}
