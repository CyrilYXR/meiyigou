package com.meiyigou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.meiyigou.pojo.TbItem;
import com.meiyigou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map map = new HashMap();

        //高亮显示
        HighlightQuery query = new SimpleHighlightQuery();
        //条件查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //设置高亮的域
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        //设置高亮数据的前缀和后缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);
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
        return map;
    }
}
