package com.fh.shop.api.goods.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CronSku implements Serializable {
    private Long id;
    private String skuName;
    private BigDecimal price;
    private Integer stock;
    private String cateName;
    private String brandName;
}
