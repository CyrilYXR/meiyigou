package com.meiyigou.content.service;

/**
 * 生成静态化页面service
 */
public interface ItemPageService {

    /**
     * 生成商品详情页
     * @param goodsId
     * @return
     */
    boolean genItemHtml(Long goodsId);

    /**
     * 删除商品详情页
     * @param goodsIds
     * @return
     */
    boolean deleteItemHtml(Long[] goodsIds);
}
