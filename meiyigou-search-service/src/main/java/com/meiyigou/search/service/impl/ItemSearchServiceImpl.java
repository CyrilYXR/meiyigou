package com.meiyigou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.meiyigou.pojo.TbItem;
import com.meiyigou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map map = new HashMap();
        //空格处理
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ",""));

        //1.查询列表
        map.putAll(searchList(searchMap));

        //2.分组查询商品分类列表
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);

        //3.查询品牌和规格列表
        String category = (String) searchMap.get("category");
        if(!"".equals(category)){
            map.putAll(searchBrandAneSpecList(category));
        }else {
            if (categoryList.size() > 0) {
                map.putAll(searchBrandAneSpecList(categoryList.get(0)));
            }
        }

        return map;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIds) {
        SolrDataQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIds);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    //查询列表
    private Map searchList(Map searchMap){
        Map map = new HashMap();

        //高亮显示
        HighlightQuery query = new SimpleHighlightQuery();
        //设置高亮的域
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        //设置高亮数据的前缀和后缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);

        //关键字过滤
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //按商品分类过滤
        if(!"".equals(searchMap.get("category"))) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //按品牌分类过滤
        if(!"".equals(searchMap.get("brand"))) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //按规格过滤
        if(searchMap.get("spec")!=null){
            Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
            for(String key : specMap.keySet()){
                FilterQuery filterQuery = new SimpleFilterQuery();
                //动态域 item_spec_*
                Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //价格过滤
        if(!"".equals(searchMap.get("price"))){
            String priceStr = (String) searchMap.get("price");
            String[] price = priceStr.split("-");
            if(!price[0].equals("0")){
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if(!price[1].equals("*")){
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //分页
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo == null){
            pageNo = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if(pageSize == null){
            pageSize = 20;
        }

        //起始索引
        query.setOffset((pageNo-1)*pageSize);
        //每页记录数
        query.setRows(pageSize);

        //排序
        String sortValue = (String) searchMap.get("sort"); //升序ASC,降序DESC
        String sortField = (String) searchMap.get("sortField");  //排序字段
        if(sortValue != null && !"".equals(sortField)){
            if("ASC".equals(sortValue)){
                Sort sort = new Sort(Sort.Direction.ASC, "item_"+sortField);
                query.addSort(sort);
            }
            if("DESC".equals(sortValue)){
                Sort sort = new Sort(Sort.Direction.DESC, "item_"+sortField);
                query.addSort(sort);
            }
        }


        //高亮页对象
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //高亮入口集合
        List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();

        for(HighlightEntry<TbItem> entry : highlighted){
            List<HighlightEntry.Highlight> highlights = entry.getHighlights();
            if(highlights.size()>0 && highlights.get(0).getSnipplets().size()>0){
                TbItem entity = entry.getEntity();
                entity.setTitle(highlights.get(0).getSnipplets().get(0));
            }
        }

        map.put("rows", page.getContent());
        map.put("totalPages",page.getTotalPages());  //总页数
        map.put("total",page.getTotalElements());  //总记录数
        return map;
    }

    /**
     * 查询商品分类列表
     */
    private List<String> searchCategoryList(Map searchMap){

        List<String> list = new ArrayList();

        Query query = new SimpleQuery("*:*");
        //关键字查询 where
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //group by
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        //获取分组页
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);

        //获取分组结果页
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");

        //获取分组入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //获取分组入口集合
        List<GroupEntry<TbItem>> entries = groupEntries.getContent();

        for(GroupEntry<TbItem> entry : entries){
            //将分组结果添加到返回结果中
            list.add(entry.getGroupValue());
        }

        return list;
    }

    /**
     * 查询品牌和规格列表
     * @param category 商品分类名称
     * @return
     */
    private Map searchBrandAneSpecList(String category){

        Map map = new HashMap();

        //1.根据品牌分类查询模板id
        Long templateId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if(templateId != null){
            //2.根据模板id查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(templateId);
            map.put("brandList", brandList);
            //3.根据模板id查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(templateId);
            map.put("specList", specList);
        }

        return map;
    }
}
