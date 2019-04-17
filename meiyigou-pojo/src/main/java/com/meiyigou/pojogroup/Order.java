package com.meiyigou.pojogroup;

import com.meiyigou.pojo.TbOrder;
import com.meiyigou.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * 订单
 */
public class Order implements Serializable {

    private TbOrder order;

    private List<TbOrderItem> orderItemList;

    public TbOrder getOrder() {
        return order;
    }

    public void setOrder(TbOrder order) {
        this.order = order;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
