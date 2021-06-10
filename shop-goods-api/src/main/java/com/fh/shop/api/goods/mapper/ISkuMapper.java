package com.fh.shop.api.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fh.shop.api.goods.vo.CronSku;
import com.fh.shop.api.goods.po.Sku;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ISkuMapper extends BaseMapper<Sku> {
    List<Sku> findRecommendNewProductList();

    List<CronSku> selectCronSku(int stockLimit);

    Integer subtractStock(@Param("skuId") Long skuId, @Param("count") Long count);

    void returnStock(@Param("skuId") Long skuId, @Param("count") Long count);

    void updateSalesVolume(@Param("skuId") Long skuId, @Param("salesVolume") Long salesVolume);
}

