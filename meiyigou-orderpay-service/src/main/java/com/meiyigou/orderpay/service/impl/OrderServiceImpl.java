package com.meiyigou.orderpay.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.meiyigou.mapper.TbOrderItemMapper;
import com.meiyigou.mapper.TbPayLogMapper;
import com.meiyigou.pojo.*;
import com.meiyigou.pojogroup.Cart;
import com.meiyigou.pojogroup.Order;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.meiyigou.mapper.TbOrderMapper;
import com.meiyigou.pojo.TbOrderExample.Criteria;
import com.meiyigou.orderpay.service.OrderService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import util.IdWorker;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;


	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
    private TbOrderItemMapper orderItemMapper;

	@Autowired
	private TbPayLogMapper payLogMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private IdWorker idWorker;

	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {

        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

        //订单id集合
        List<String> orderIdList = new ArrayList<>();

        //支付金额（总的订单金额累加）
		double total_money = 0;

        for(Cart cart : cartList){
            long orderId = idWorker.nextId();
            System.out.println("sellerId:"+cart.getSellerId());
            TbOrder tbOrder=new TbOrder();//新创建订单对象
            tbOrder.setOrderId(orderId);//订单ID
            tbOrder.setUserId(order.getUserId());//用户名
            tbOrder.setPaymentType(order.getPaymentType());//支付类型
            tbOrder.setStatus("1");//状态：未付款
            tbOrder.setCreateTime(new Date());//订单创建日期
            tbOrder.setUpdateTime(new Date());//订单更新日期
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());//地址
            tbOrder.setReceiverMobile(order.getReceiverMobile());//手机号
            tbOrder.setReceiver(order.getReceiver());//收货人
            tbOrder.setSourceType(order.getSourceType());//订单来源
            tbOrder.setSellerId(cart.getSellerId());//商家ID

            //循环购物车明细
            double money=0;
            for(TbOrderItem orderItem :cart.getOrderItemList()){
                orderItem.setId(idWorker.nextId());
                orderItem.setOrderId(orderId);//订单ID
                orderItem.setSellerId(cart.getSellerId());
                money+=orderItem.getTotalFee().doubleValue();//金额累加
                orderItemMapper.insert(orderItem);
            }
            tbOrder.setPayment(new BigDecimal(money));
            orderMapper.insert(tbOrder);
            orderIdList.add(orderId+"");
            total_money+=money;
        }

        //添加支付日志
		if(order.getPaymentType().equals("1")){
			TbPayLog payLog = new TbPayLog();
			//支付订单号
			payLog.setOutTradeNo(idWorker.nextId()+"");
			payLog.setCreateTime(new Date());
			payLog.setUserId(order.getUserId());
			payLog.setOrderList(orderIdList.toString().replace("[","").replace("]",""));
			payLog.setTotalFee((long) (total_money*100));  //支付金额
			payLog.setTradeState("0");  //交易状态
			payLog.setPayType("1");  //支付类型
			payLogMapper.insert(payLog);
			redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);
		}

        redisTemplate.boundHashOps("cartList").delete(order.getUserId());

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
	@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public TbPayLog searchPayLogFromRedis(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        //修改支付日志
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setTradeState("1");  //交易成功
        payLog.setPayTime(new Date());
        payLog.setTransactionId(transaction_id);

        payLogMapper.updateByPrimaryKey(payLog);

        //修改订单表状态
        String orderList = payLog.getOrderList();
        String[] orderIds = orderList.split(",");

        for(String orderId : orderIds){
            TbOrder order = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));
            order.setStatus("2");  //已付款状态
            order.setPaymentTime(new Date());
            orderMapper.updateByPrimaryKey(order);
        }

        //清除缓存中的payLog
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());

        System.out.println("=====执行更新订单和支付日志表信息");

    }

	@Override
	public PageResult findOrderPage(TbOrder tbOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(tbOrder.getUserId());
        if(tbOrder!=null){
            //如果状态为空表示查询全部
            if(tbOrder.getStatus()!=null && tbOrder.getStatus().length()>0){
                criteria.andStatusEqualTo(tbOrder.getStatus());
            }
        }

        Page<TbOrder> tbOrderPage = (Page<TbOrder>) orderMapper.selectByExample(example);
        List<TbOrder> tbOrderList = tbOrderPage.getResult();

        List<Order> orders = new ArrayList<>();
        for(TbOrder orderItem:tbOrderList){
            Order order = new Order();
            order.setOrder(orderItem);
            TbOrderItemExample itemExample = new TbOrderItemExample();
            TbOrderItemExample.Criteria itemCriteria = itemExample.createCriteria();
            itemCriteria.andOrderIdEqualTo(orderItem.getOrderId());
            List<TbOrderItem> tbOrderItems = orderItemMapper.selectByExample(itemExample);
            order.setOrderItemList(tbOrderItems);
            orders.add(order);
        }

        return new PageResult(tbOrderPage.getTotal(), orders);

	}


}
