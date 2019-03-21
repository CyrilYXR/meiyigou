package com.meiyigou.solrutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.stereotype.Component;

@Component
public class CleanAllData {

    @Autowired
    private SolrTemplate solrTemplate;

    //清除solr所有数据
    private void cleanAllData(){
        SolrDataQuery solrDataQuery = new SimpleQuery("*:*");
        solrTemplate.delete(solrDataQuery);
        solrTemplate.commit();
    }

    public static void main(String[] args) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/application*.xml");
        CleanAllData cleanAllData = (CleanAllData) applicationContext.getBean("cleanAllData");
        cleanAllData.cleanAllData();
    }
}
