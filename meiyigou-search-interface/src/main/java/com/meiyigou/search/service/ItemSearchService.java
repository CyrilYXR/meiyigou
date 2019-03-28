package com.meiyigou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * 查询
     * @param searchMap
     * @return
     */
    Map<String, Object> search(Map searchMap);

    /**
     * 导入列表
     * @param goodsIds
     */
    void importList(List goodsIds);

    /**
     * 删除商品列表
     * @param goodsIds SPU
     */
    void deleteByGoodsIds(List goodsIds);
}
