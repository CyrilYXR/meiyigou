package com.meiyigou.solrutil;

import com.alibaba.fastjson.JSON;
import com.meiyigou.mapper.TbItemMapper;
import com.meiyigou.pojo.TbItem;
import com.meiyigou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private TbItemMapper itemMapper;

    public void importItemData(){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = itemMapper.selectByExample(example);

        System.out.println("====导入商品表数据到solr库====");
        for(TbItem item : tbItems){
            Map map = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(map);
            System.out.println(item.getId() + " " + item.getTitle() + " " + item.getPrice() + " " + item.getSpecMap());
        }

        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
        System.out.println("====导入数据结束====");
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) applicationContext.getBean("solrUtil");
        solrUtil.importItemData();
    }
}
