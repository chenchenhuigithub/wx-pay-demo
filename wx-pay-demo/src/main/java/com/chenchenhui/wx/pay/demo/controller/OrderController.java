package com.chenchenhui.wx.pay.demo.controller;

import com.chenchenhui.wx.pay.demo.mapper.order.OrderMapper;
import com.chenchenhui.wx.pay.demo.model.BizResponse;
import com.chenchenhui.wx.pay.demo.model.Order;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {


    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private WxPayService payService;


    /*
    *
    * 用户订单列表
    *
    * */
    @GetMapping
    public BizResponse orderList(@RequestParam String openid){

        List<Order> orderlist = orderMapper.orderlist(openid);

        return BizResponse.success(orderlist);
    }

    /*
    * 生成订单
    * */
    @PostMapping
    public BizResponse push(@RequestBody String openid){

        //订单内容
        Order order = new Order();

        order.setOpenid(openid);

        //生成订单ID（不安全！！）
        order.setOrderId(new Date().getTime());

        orderMapper.push(order);

        return BizResponse.success();
    }

    /*
    *
    * 订单支付
    *
    * */
        @GetMapping("/pay")
    public BizResponse pay(@RequestParam String openid,@RequestParam Long orderId) throws WxPayException {
        //这里懒了点，直接用orderId当商户支付的订单号(outTradeNo)，正规代码如下：
        /*
        支付的订单号生成：

        UUID uuid = UUID.randomUUID();
        StringBuffer outTradeNo = new StringBuffer();
        Arrays.stream(uuid.toString().split("-")).forEach(temp -> outTradeNo.append(temp));
        System.out.println("uuid = " + outTradeNo);


        填入下面代码中： .outTradeNo(outTradeNo.toString())
        * */
        WxPayUnifiedOrderRequest payUnifiedOrderRequest = WxPayUnifiedOrderRequest.newBuilder()
                // 商品描述
                .body("微信支付-demo")
                // 金额:单位（分）
                .totalFee(1)
                // 终端IP -- 支持IPV4和IPV6两种格式的IP地址。调用微信支付API的机器IP
                .spbillCreateIp("终端IP")
                // 回调地址 -- 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。==> 后台配置
                .notifyUrl("指定回调地址/order/pay")
                // 交易类型 -- JSAPI公众号支付、NATIVE原生扫码支付、APP--app支付，统一下单接口trade_type的传参可参考这里
                .tradeType(WxPayConstants.TradeType.JSAPI)
                // 用户标识
                .openid(openid)
                //商户支付的订单号由商户自定义生成，仅支持使用字母、数字、中划线-、下划线_、竖线|、星号*这些英文半角字符的组合，请勿使用汉字或全角等特殊字符。
                .outTradeNo(orderId.toString())
                .build();
        //生成预支付订单和小程序支付接口所需的参数
        Object order = payService.createOrder(payUnifiedOrderRequest);
        return BizResponse.success(order);
    }

    /*
    *
    * 订单支付回调
    *
    * */
    @PostMapping("/pay")
    public String callback(@RequestBody String xmlData) throws WxPayException {
        WxPayOrderNotifyResult result = payService.parseOrderNotifyResult(xmlData);
        System.out.println("result ===>  " + result);
        String returnCode = result.getReturnCode();
        System.out.println("returnCode ===> " + result.getReturnCode());
        //returnCode == SUCCESS 则成功
        if (StringUtils.isNotEmpty(returnCode) && returnCode.equals("SUCCESS")) {
            //修改订单状态
            //这里的result.getOutTradeNo() 是前面生成outTradeNo
            orderMapper.updateByOrderId(1, result.getOutTradeNo());
            //向微信服务器回复SUCCESS, 微信服务器便会停止回调(不回复-微信会间隔性回调, 直到正确回复
            return "SUCCESS";
        }
        //如果不匹配, 回复FAIL即可
        return "FAIL";
    }


    /*
    *
    * 订单退款
    *
    * */
    @GetMapping("/refund")
    public BizResponse refund(@RequestParam("orderId") String orderId) throws WxPayException {
        Order order = orderMapper.getOrder(orderId);
        //这里跟上面一样，商户退款单号(outRefundNo)，正规代码如下：
        /*
        商户退款单号生成：

        UUID uuid = UUID.randomUUID();
        StringBuffer outRefundNo = new StringBuffer();
        Arrays.stream(uuid.toString().split("-")).forEach(temp -> outRefundNo.append(temp));
        System.out.println("outRefundNo = " + outRefundNo);

        填入下面代码中：.outRefundNo(outRefundNo.toString())
        * */
        WxPayRefundRequest refundRequest = WxPayRefundRequest.newBuilder()
                //商户订单号 --商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
                .outTradeNo(orderId)
                //商户退款单号 --商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
                .outRefundNo(orderId)
                //订单总金额，单位为分，只能为整数
                .totalFee(order.getTotal().intValue())
                //退款总金额，订单总金额，单位为分，只能为整数
                .refundFee(order.getTotal().intValue())
                //退款回调
                .notifyUrl("指定回调地址/user/noRefund")
                .build();
        payService.refund(refundRequest);
        return BizResponse.success();
    }

    /*
    *
    * 退款回调
    *
    * */
    @PostMapping("/noRefund")
    public String refundCallback(@RequestBody String xmlData) throws WxPayException {
        WxPayRefundNotifyResult result = payService.parseRefundNotifyResult(xmlData);
        System.out.println("退款result = " + result);

        String returnCode = result.getReturnCode();
        System.out.println("returnCode = " + result.getReturnCode());
        //returnCode == SUCCESS 则成功
        if (StringUtils.isNotEmpty(returnCode) && returnCode.equals("SUCCESS")) {
            //修改订单状态
            //这里的getOutTradeNo() 是前面生成outTradeNo
            orderMapper.updateByOrderId(2, result.getReqInfo().getOutTradeNo());
            //向微信服务器回复SUCCESS, 微信服务器便会停止回调(不回复-微信会间隔性回调, 直到正确回复
            return "SUCCESS";
        }
        //如果不匹配, 回复FAIL即可
        return "FAIL";

    }
}
