package com.meiyigou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.meiyigou.orderpay.service.AliPayService;
import com.meiyigou.orderpay.service.OrderService;
import com.meiyigou.pojo.TbPayLog;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private AliPayService aliPayService;

    @Reference
    private OrderService orderService;

    @RequestMapping(value = "/goAlipay", produces = "text/html; charset=UTF-8")
    public String goAlipay(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        TbPayLog payLog = orderService.searchPayLogFromRedis(username);
        if(payLog != null) {
            return aliPayService.goAlipay(payLog.getOutTradeNo(), payLog.getTotalFee());
        } else {
            return null;
        }
    }
}
