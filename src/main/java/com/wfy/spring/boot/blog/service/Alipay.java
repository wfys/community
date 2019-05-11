package com.wfy.spring.boot.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.wfy.spring.boot.blog.util.AlipayBean;
import com.wfy.spring.boot.blog.util.AlipayProperties;

@Component
public class Alipay {

    /**
     *支付接口
     * @param alipayBean
     * @return
     * @throws AlipayApiException
     */
    @Autowired
    AlipayProperties alipayProperties;
    public String pay(AlipayBean alipayBean) throws AlipayApiException {
        // AlipayClient
        String serverUrl = alipayProperties.getGatewayUrl();
        String appId = alipayProperties.getApp_id();
        String privateKey = alipayProperties.getPrivateKey();
        String format = "json";
        String charset = alipayProperties.getCharset();
        String alipayPublicKey = alipayProperties.getPublicKey();
        String signType = alipayProperties.getSign_type();
        String returnUrl = alipayProperties.getReturn_url();
        String notifyUrl = alipayProperties.getNotify_url();
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType);
        // 2订单号
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        // 同步回调地址
        alipayRequest.setReturnUrl(returnUrl);
        // 异步回调地址
        alipayRequest.setNotifyUrl(notifyUrl);
        alipayRequest.setBizContent(JSON.toJSONString(alipayBean));
        String result = alipayClient.pageExecute(alipayRequest).getBody();
        return result;
    }
}
