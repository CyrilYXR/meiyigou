package com.meiyigou.usercart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.meiyigou.mapper.TbItemMapper;
import com.meiyigou.pojo.TbItem;
import com.meiyigou.pojo.TbOrderItem;
import com.meiyigou.pojogroup.Cart;
import com.meiyigou.usercart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //1.根据SKU的id查询商品明细对象
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if(item==null){
            throw new RuntimeException("商品不存在");
        }
        if(!item.getStatus().equals("1")){
            throw new RuntimeException("商品状态不合法");
        }

        //2.根据SKU的id获得商家id
        String sellerId = item.getSellerId();

        //3.根据商家id在购物车列表中查询购物车对象
        Cart cart = searchCartBySellerId(cartList, sellerId);

        if(cart==null) {
            //4.如果购物车列表中不存在该商家id
            //4.1 创建一个新的购物车对象
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());

            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(item, num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);

            //4.2 将新的购物车对象添加到购物车列表中
            cartList.add(cart);

        } else {
            //5.如果购物车列表中存在该商家id
            //  判断该商品是否在该购物车的明细列表中存在
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            if(orderItem == null) {
                //5.1 如果不存在，创建新的购物车明细对象，并添加到该购物车的明细列表中
                orderItem = createOrderItem(item, num);
                cart.getOrderItemList().add(orderItem);

            } else {
                //5.2 如果存在，在原有的数量上加num，并更新金额
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum() * orderItem.getPrice().doubleValue()));

                //当明细的数量小于等于0，移除
                if(orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);
                }

                //当购物车的明细数量为0，在购物车列表中移除购物车
                if(cart.getOrderItemList().size()==0){
                    cartList.remove(cart);
                }
            }
        }

        return cartList;
    }

    @Override
    public List<Cart> findCartListFromRedis(String userName) {

        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(userName);
        System.out.println("=====从购物车中提取购物车" + cartList);
        if(cartList==null){
            cartList = new ArrayList<>();
        }

        return cartList;
    }

    @Override
    public void saveCartListToRedis(String userName, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(userName, cartList);
        System.out.println("=====将购物车存入Redis");
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {

        for(Cart cart : cartList2){
            for(TbOrderItem orderItem : cart.getOrderItemList()){
                cartList1 = addGoodsToCartList(cartList1, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return cartList1;
    }

    /**
     * 根据商家id在购物车列表中查询购物车对象
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId){
        for(Cart cart : cartList){
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    /**
     * 创建购物车明细对象
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num){
        //购物车明细对象
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;
    }

    /**
     * 根据SKU id在购物车列表中查询订单明细对象
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId){
        for(TbOrderItem orderItem:orderItemList){
            if(orderItem.getItemId().longValue() == itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }
}
