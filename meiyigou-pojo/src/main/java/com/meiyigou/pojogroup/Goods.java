package com.meiyigou.pojogroup;

import com.meiyigou.pojo.TbGoods;
import com.meiyigou.pojo.TbGoodsDesc;
import com.meiyigou.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

public class Goods implements Serializable {

    //商品SPU
    private TbGoods goods;
    //商品扩展信息
    private TbGoodsDesc goodsDesc;
    //商品SKU
    private List<TbItem> itemList;

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }
}
