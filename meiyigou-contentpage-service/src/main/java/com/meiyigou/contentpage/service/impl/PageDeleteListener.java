package com.meiyigou.contentpage.service.impl;

import com.meiyigou.contentpage.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * JMS监听类，用于删除静态页面
 */
@Component
public class PageDeleteListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {

        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long[] goodsIds = (Long[]) objectMessage.getObject();
            System.out.println("=====接收到订阅的meiyigou_topic_page_delete的消息: " + goodsIds);
            boolean b = itemPageService.deleteItemHtml(goodsIds);
            System.out.println("=====删除网页" + goodsIds +" : "+ b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
