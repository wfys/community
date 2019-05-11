package com.wfy.spring.boot.blog.util;

/*
 * 支付宝APId
 * @author wfy
 *
 */
public class AlipayBean {

    /**
     * 订单号（必填）
     *
     */
    private String out_trade_no;
    /**
     * 描述信息（必填）
     */
    private String subject;
    /**
     * 价钱（必填）
     */
    private String total_amount;
    /**
     * 描述（必填）
     */
    private String body;
    /**
     * 支付时间
     */
    private String timeout_express= "5m";
    /**
     * 产品号
     */
    private String product_code= "FAST_INSTANT_TRADE_PAY";
    public AlipayBean(String out_trade_no,String subject,String total_amount,String body){
    	this.out_trade_no=out_trade_no;
    	this.subject=subject;
    	this.total_amount=total_amount;
    	this.body=body;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }
    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getTotal_amount() {
        return total_amount;
    }
    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public String getTimeout_express() {
        return timeout_express;
    }
    public void setTimeout_express(String timeout_express) {
        this.timeout_express = timeout_express;
    }
    public String getProduct_code() {
        return product_code;
    }
    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

}
