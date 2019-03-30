package com.meiyigou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.meiyigou.contentpage.service.ItemPageService;
import com.meiyigou.pojo.TbGoods;
import com.meiyigou.pojo.TbItem;
import com.meiyigou.pojogroup.Goods;
import com.meiyigou.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Destination queueSolrDestination;

	@Autowired
    private Destination queueSolrDeleteDestination;

	@Autowired
    private Destination topicPageDestination;

	@Autowired
    private Destination topicPageDeleteDestination;

	/*@Reference(timeout = 10000)
    private ItemSearchService itemSearchService;*/
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	/*@RequestMapping("/add")
	public Result add(@RequestBody TbGoods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}*/
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);

			//从索引库中删除
            //itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
            //JMS实现  发布
            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });

            //删除每个服务器上的商品详情页
            jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });

			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

	/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}

	/**
	 * 批量修改状态
	 * @param ids
	 * @param status
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long ids[], String status){

		try {
			goodsService.updateStatus(ids, status);

			if("1".equals(status)){ //审核通过
			    //导入到索引库
			    //得到需要导入的SKU列表
				List<TbItem> list = goodsService.findItemListByGoodsIdListAndStatus(ids, status);
				//转换成JSON字符串
                final String jsonString = JSON.toJSONString(list);
                if(list.size()>0) {
                    //导入列表到solr索引库
                    //itemSearchService.importList(list);
                    //JMS实现解耦
					jmsTemplate.send(queueSolrDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(jsonString);
						}
					});
                }

                //生成商品详情页
                /*for(Long goodsId : ids) {
                    itemPageService.genItemHtml(goodsId);
                }*/

                //JMS实现生成商品详情页
                for(final Long goodsId : ids){
                    jmsTemplate.send(topicPageDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return (Message) session.createTextMessage(goodsId+"");
                        }
                    });
                }


			}

			return new Result(true, "成功");
		} catch (Exception e) {
			return new Result(false, "失败");
		}
	}

	@Reference(timeout = 40000)
    private ItemPageService itemPageService;

	@RequestMapping("/genHtml")
	public void genHtml(Long goodsId){
        itemPageService.genItemHtml(goodsId);
    }
}
