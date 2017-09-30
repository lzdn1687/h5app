package com.kesitong.shop.util;

/**
 * 天朗信息技术有限公司
 *
 * @author liuzongze
 *         Created by Asus on 2017/9/21.
 * @link http://www.jiahetianlang.com
 * 常量类
 */
public class Constants {
    //微信APP_ID
    public static final String WX_APP_ID = "wxe26e08c4e9d46c4c";

    //支付宝APP_ID
    public static final String ALIPAY_APP_ID = "2017041506737786";

    //微信支付接口
    public static final String WX_PAY_URL = "http://jh.dzso.com/wxpay.api.php?re=wxpay";
//    public static final String WX_PAY_URL = "http://jh.dzso.com/pay/?m=payment&s=transfer&re=wxpay";

    //支付成功
    public static final String PAY_RECEIVER_ACTION_SUCCESS = "pay_receiver_action_success";
    //支付失败
    public static final String PAY_RECEIVER_ACTION_FAIL = "pay_receiver_action_fail";
    //交易取消
    public static final String PAY_RECEIVER_ACTION_CANCEL = "pay_receiver_action_cancel";
}
