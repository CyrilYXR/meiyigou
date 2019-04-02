package com.meiyigou.orderpay.service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface AliPayService {

    String goAlipay(String outTradeNo, Long totalFee);
}
