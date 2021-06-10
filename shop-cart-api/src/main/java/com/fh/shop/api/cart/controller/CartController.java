package com.fh.shop.api.cart.controller;

import com.fh.common.ServerResponse;
import com.fh.shop.api.BaseController;
import com.fh.shop.api.cart.biz.ICartService;
import com.fh.shop.api.member.vo.MemberVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/carts")
@Api(tags = "购物车api")
public class CartController extends BaseController {

    @Resource(name = "cartService")
    private ICartService cartService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping("addCartItem")
    @ApiOperation("向购物车中增加商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skuId", value = "商品id", dataType = "java.lang.Long", required = true),
            @ApiImplicitParam(name = "count", value = "增加的数量", dataType = "java.lang.Long", required = true),
            @ApiImplicitParam(name = "token", value = "头信息", dataType = "java.lang.String", required = true,paramType = "header")
    })
    public ServerResponse addCartItem(Long skuId,Long count){
        MemberVo memberVo = buildMemberVo(request);
        Long memberId = memberVo.getId();
        return cartService.addCartItem(skuId,count,memberId);
    }

    @GetMapping("findCart")
    @ApiOperation("查询购物车的商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "头信息", dataType = "java.lang.String", required = true,paramType = "header")
    })
    public ServerResponse findCart(){
        MemberVo memberVo = buildMemberVo(request);
        Long memberId = memberVo.getId();
        return cartService.findCart(memberId);
    }

    @GetMapping("findCartTotalCount")
    @ApiOperation("查询购物车的商品件数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "头信息", dataType = "java.lang.String", required = true,paramType = "header")
    })
    public ServerResponse findCartTotalCount(){
        MemberVo memberVo = buildMemberVo(request);
        Long memberId = memberVo.getId();
        return cartService.findCartTotalCount(memberId);
    }

    @DeleteMapping("deleteCartItem")
    @ApiOperation("删除购物车中的指定商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "头信息", dataType = "java.lang.String", required = true,paramType = "header")
    })
    public ServerResponse deleteCartItem(Long skuId){
        MemberVo memberVo = buildMemberVo(request);
        Long memberId = memberVo.getId();
        return cartService.deleteCartItem(memberId,skuId);
    }

    @DeleteMapping("deleteBatchCartItem")
    @ApiOperation("批量删除购物车中的指定商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "头信息", dataType = "java.lang.String", required = true,paramType = "header")
    })
    public ServerResponse deleteBatchCartItem(String ids){
        MemberVo memberVo = buildMemberVo(request);
        Long memberId = memberVo.getId();
        return cartService.deleteBatchCartItem(memberId,ids);
    }
}
