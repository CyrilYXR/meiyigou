package com.meiyigou.contentpage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.meiyigou.contentpage.service.ItemPageService;
import com.meiyigou.mapper.TbGoodsDescMapper;
import com.meiyigou.mapper.TbGoodsMapper;
import com.meiyigou.mapper.TbItemCatMapper;
import com.meiyigou.mapper.TbItemMapper;
import com.meiyigou.pojo.TbGoods;
import com.meiyigou.pojo.TbGoodsDesc;
import com.meiyigou.pojo.TbItem;
import com.meiyigou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成静态页面实现类
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean genItemHtml(Long goodsId) {

        Configuration configuration = freeMarkerConfig.getConfiguration();
        try {
            Template template = configuration.getTemplate("item.ftl");

            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            //读取商品分类
            String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();

            //读取SKU列表信息
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");
            //按是否默认字段排序，返回的第一条记录为默认SKU
            example.setOrderByClause("is_default desc");
            List<TbItem> itemList = itemMapper.selectByExample(example);

            //创建数据模型
            Map dataModel = new HashMap();
            dataModel.put("goods", goods);
            dataModel.put("goodsDesc", goodsDesc);
            dataModel.put("itemCat1", itemCat1);
            dataModel.put("itemCat2", itemCat2);
            dataModel.put("itemCat3", itemCat3);
            dataModel.put("itemList", itemList);

            Writer out = new FileWriter(pagedir+goodsId+".html");

            template.process(dataModel, out);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (TemplateException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteItemHtml(Long[] goodsIds) {
        try{
            for(Long goodsId : goodsIds){
                new File(pagedir+goodsId+".html").delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
