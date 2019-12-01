package com.chenchenhui.wx.pay.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    //订单ID
    private Long orderId;

    //openid
    private String openid;

    //状态
    private Integer state;

    //金额
    private Double total;


}
