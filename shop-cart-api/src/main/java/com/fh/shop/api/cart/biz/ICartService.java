package com.fh.shop.api.cart.biz;

import com.fh.common.ServerResponse;

public interface ICartService {
    ServerResponse addCartItem(Long skuId, Long count, Long memberId);

    ServerResponse findCart(Long memberId);

    ServerResponse findCartTotalCount(Long memberId);

    ServerResponse deleteCartItem(Long memberId, Long skuId);

    ServerResponse deleteBatchCartItem(Long memberId, String ids);
}
