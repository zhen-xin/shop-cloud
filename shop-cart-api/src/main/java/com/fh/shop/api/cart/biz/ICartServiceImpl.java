package com.fh.shop.api.cart.biz;

import com.alibaba.fastjson.JSON;
import com.fh.common.Constants;
import com.fh.common.ResponseEnum;
import com.fh.common.ServerResponse;
import com.fh.shop.api.cart.vo.CartItemVo;
import com.fh.shop.api.cart.vo.CartVo;
import com.fh.shop.api.goods.IGoodsFeignService;
import com.fh.shop.api.goods.po.Sku;
import com.fh.util.BigDecimalUtil;
import com.fh.util.KeyUtil;
import com.fh.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service("cartService")
@Transactional(rollbackFor = Exception.class)
public class ICartServiceImpl implements ICartService {

    @Autowired
    private IGoodsFeignService goodsFeign;

    @Value("${cart.count.limit}")
    private int countLimit;

    @Override
    public ServerResponse addCartItem(Long skuId, Long count, Long memberId) {
        //限制用户只能购买十件该商品
        if(count > countLimit){
            return ServerResponse.error(ResponseEnum.CART_SKU_COUNT_LIMIT);
        }
        //查询商品是否存在
        ServerResponse<Sku> skuServerResponse = goodsFeign.findSkuById(skuId);
        Sku sku = skuServerResponse.getData();
        if(sku == null){
            return ServerResponse.error(ResponseEnum.CART_SKU_IS_NULL);
        }
        //商品是否上架
        if(sku.getIfGrounding() == Constants.IF_GROUNDING_DOWN){
            return ServerResponse.error(ResponseEnum.CART_SKU_IS_DOWN);
        }
        //商品库存量大于等于购买的输量
        if(sku.getStock() < count){
            return ServerResponse.error(ResponseEnum.CART_STOCK_IS_NOT);
        }
        //购物车是否存在
        String key = KeyUtil.buildCartKey(memberId.toString());
        String cartVoJson = RedisUtil.hget(key,Constants.CART_JSON_FIELD);
        if(StringUtils.isEmpty(cartVoJson)){
            if(count <= 0){
                return ServerResponse.error(ResponseEnum.CART_IS_ERROR);
            }
            //购物车不存在直接添加商品进购物车
            CartItemVo cartItemVo = new CartItemVo();
            cartItemVo.setCount(count);
            String price = sku.getPrice().toString();
            cartItemVo.setPrice(price);
            cartItemVo.setSkuId(skuId);
            cartItemVo.setSkuImage(sku.getImage());
            cartItemVo.setSkuName(sku.getSkuName());
            String subPrice = BigDecimalUtil.mul(price, count.toString()).toString();
            cartItemVo.setSubPrice(subPrice);

            CartVo cartVo = new CartVo();
            cartVo.getCartItemVoList().add(cartItemVo);
            cartVo.setTotalCount(count);
            cartVo.setTotalPrice(subPrice);

            //更新购物车
            Map<String,String> map = new HashMap<>();
            map.put(Constants.CART_JSON_FIELD, JSON.toJSONString(cartVo));
            map.put(Constants.CART_COUNT_FIELD, count.toString());
            RedisUtil.hmset(key,map);
        }else {
            //购物车存在
            CartVo cartVo = JSON.parseObject(cartVoJson, CartVo.class);
            List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
            //判断购物车中是否有该商品
            Optional<CartItemVo> optionalCartItemVo = cartItemVoList.stream().filter(x -> x.getSkuId().longValue() == skuId.longValue()).findFirst();
            if(optionalCartItemVo.isPresent()){
                //购物车存在,购物车中有该商品
                CartItemVo cartItemVo = optionalCartItemVo.get();
                long itemCount = cartItemVo.getCount() + count;
                //限制用户只能购买十件该商品
                if(itemCount > countLimit){
                    return ServerResponse.error(ResponseEnum.CART_SKU_COUNT_LIMIT);
                }
                //判断商品个数是否小于0
                if(itemCount <= 0){
                    cartItemVoList.removeIf(x -> x.getSkuId().longValue() == skuId.longValue());
                }else {
                    cartItemVo.setCount(itemCount);
                    BigDecimal subPrice = BigDecimalUtil.mul(cartItemVo.getPrice(), cartItemVo.getCount() + "");
                    cartItemVo.setSubPrice(subPrice.toString());
                }
                //更新购物车
                updateCart(key, cartVo);
            }else {
                if(count <= 0){
                    return ServerResponse.error(ResponseEnum.CART_IS_ERROR);
                }
                //购物车存在,购物车中没有该商品
                CartItemVo cartItemVo = new CartItemVo();
                cartItemVo.setCount(count);
                String price = sku.getPrice().toString();
                cartItemVo.setPrice(price);
                cartItemVo.setSkuId(skuId);
                cartItemVo.setSkuImage(sku.getImage());
                cartItemVo.setSkuName(sku.getSkuName());
                String subPrice = BigDecimalUtil.mul(price, count.toString()).toString();
                cartItemVo.setSubPrice(subPrice);
                cartVo.getCartItemVoList().add(cartItemVo);
                //更新购物车
                updateCart(key, cartVo);
            }
        }
        return ServerResponse.success();
    }

    private void updateCart(String key, CartVo cartVo) {
        //计算cartVo
        long totalCount = 0L;
        BigDecimal totalPrice = new BigDecimal(0);
        List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
        for (CartItemVo cartItem : cartItemVoList) {
            totalCount += cartItem.getCount();
            totalPrice = totalPrice.add(new BigDecimal(cartItem.getSubPrice()));
        }
        cartVo.setTotalCount(totalCount);
        cartVo.setTotalPrice(totalPrice.toString());
        //更新购物车
        if(cartItemVoList.size() == 0){
            RedisUtil.del(key);
        }else {
            Map<String,String> map = new HashMap<>();
            map.put(Constants.CART_JSON_FIELD, JSON.toJSONString(cartVo));
            map.put(Constants.CART_COUNT_FIELD, totalCount+"");
            RedisUtil.hmset(key,map);
        }
    }

    @Override
    public ServerResponse findCart(Long memberId) {
        String cartVoStr = RedisUtil.hget(KeyUtil.buildCartKey(memberId.toString()),Constants.CART_JSON_FIELD);
        if(StringUtils.isEmpty(cartVoStr)){
            return ServerResponse.error(ResponseEnum.CART_IS_NULL);
        }
        CartVo cartVo = JSON.parseObject(cartVoStr, CartVo.class);
        return ServerResponse.success(cartVo);
    }

    @Override
    public ServerResponse findCartTotalCount(Long memberId) {
        String totalCount = RedisUtil.hget(KeyUtil.buildCartKey(memberId.toString()),Constants.CART_COUNT_FIELD);
        if(StringUtils.isEmpty(totalCount)){
            return ServerResponse.error(ResponseEnum.CART_IS_NULL);
        }
        return ServerResponse.success(totalCount);
    }

    @Override
    public ServerResponse deleteCartItem(Long memberId, Long skuId) {
        //判断购物车是否存在
        String key = KeyUtil.buildCartKey(memberId.toString());
        boolean state = RedisUtil.exist(key);
        if(!state){
            return ServerResponse.error(ResponseEnum.CART_IS_NULL);
        }

        String cartJson = RedisUtil.hget(key, Constants.CART_JSON_FIELD);
        CartVo cartVo = JSON.parseObject(cartJson, CartVo.class);
        List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
        //判断购物车中是否有该商品
        Optional<CartItemVo> optionalCartItem = cartItemVoList.stream().filter(x -> x.getSkuId().longValue() == skuId.longValue()).findFirst();
        if(!optionalCartItem.isPresent()){
            return ServerResponse.error(ResponseEnum.CART_ITEM_IS_NULL);
        }
        //删除当前商品
        cartItemVoList.removeIf(x -> x.getSkuId().longValue() == skuId.longValue());
        //更新购物车
        updateCart(key,cartVo);
        return ServerResponse.success();
    }

    @Override
    public ServerResponse deleteBatchCartItem(Long memberId, String ids) {
        //判断是否选择了商品
        if(StringUtils.isEmpty(ids)){
            return ServerResponse.error(ResponseEnum.CART_BATCH_DELETE_NO_SELECT);
        }
        //判断购物车是否存在
        String key = KeyUtil.buildCartKey(memberId.toString());
        boolean state = RedisUtil.exist(key);
        if(!state){
            return ServerResponse.error(ResponseEnum.CART_IS_NULL);
        }

        String cartJson = RedisUtil.hget(key, Constants.CART_JSON_FIELD);
        CartVo cartVo = JSON.parseObject(cartJson, CartVo.class);
        List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
        //批量删除购物车中的商品
        Arrays.stream(ids.split(",")).forEach(x -> cartItemVoList.removeIf(y -> y.getSkuId().longValue() == Long.parseLong(x)));

        //更新购物车
        updateCart(key,cartVo);
        return ServerResponse.success();
    }
}
