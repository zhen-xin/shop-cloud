package com.fh.shop.api.goods.po;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class Sku implements Serializable {
    private Long id;

    private Long spuId;

    private String skuName;

    private BigDecimal price;

    private Integer stock;

    private String specInfo;

    private Long colorId;

    private String Image;

    //是否上架
    private Integer ifGrounding;
    //销量
    private Integer salesVolume;

}
