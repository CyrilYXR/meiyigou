package com.meiyigou.contentpage.service.impl;

import com.meiyigou.contentpage.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 监听类，用于生成网页
 */
@Component
public class PageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String goodsId = textMessage.getText();
            System.out.println("=====接收到订阅的meiyigou_topic_page的消息: " + goodsId);
            boolean b = itemPageService.genItemHtml(Long.parseLong(goodsId));
            System.out.println("=====静态网页生成结果" + b);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
