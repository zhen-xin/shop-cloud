package com.fh.shop.api.goods.biz;

import com.fh.common.ServerResponse;
import com.fh.shop.api.goods.vo.CronSku;

import java.util.List;

public interface ISpuService {
    ServerResponse findRecommendNewProductList();

    List<CronSku> selectCronSku(int stockLimit);

    ServerResponse findSkuById(Long id);
}
