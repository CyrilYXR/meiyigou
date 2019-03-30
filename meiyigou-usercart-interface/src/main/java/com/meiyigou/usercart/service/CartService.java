package com.meiyigou.usercart.service;

import com.meiyigou.pojogroup.Cart;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    /**
     * 添加商品到购物车列表
     * @param list 原购物车列表
     * @param itemId 商品id
     * @param num 数量
     * @return 操作后的购物车列表
     */
    List<Cart> addGoodsToCartList(List<Cart> list, Long itemId, Integer num);

    /**
     * 从Redis中提取购物车列表
     * @param userName
     * @return
     */
    List<Cart> findCartListFromRedis(String userName);

    /**
     * 将购物车列表存入Redis
     * @param userName
     * @param cartList
     */
    void saveCartListToRedis(String userName, List<Cart> cartList);

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);
}
