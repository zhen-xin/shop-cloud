package com.fh.shop.api.cart.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CartItemVo implements Serializable {
    private Long skuId;
    private String skuName;
    private String price;
    private String skuImage;
    private Long count;
    private String subPrice;
}
