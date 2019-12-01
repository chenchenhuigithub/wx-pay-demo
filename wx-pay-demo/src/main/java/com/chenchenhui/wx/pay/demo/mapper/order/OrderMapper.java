package com.chenchenhui.wx.pay.demo.mapper.order;

import com.chenchenhui.wx.pay.demo.model.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface OrderMapper {

    //生成订单
    @Insert("INSERT INTO `order` " +
            "(order_Id, openid, state, out_trade_no) " +
            "VALUES (#{orderId}, #{openid}, 0, '')")
    public void push(Order order);

    //指定用户订单列表
    @Select("SELECT order_Id, openid, state, total FROM `order` WHERE openid = #{openid}")
    public List<Order> orderlist(String openid);

    //修改订单状态
    @Update("UPDATE `order` SET state=#{state} WHERE openid = #{openid}")
    public void update(Integer state,String openid);

    //修改订单状态
    @Update("UPDATE `order` SET state=#{state} WHERE order_id = #{orderId}")
    public void updateByOrderId(Integer state,String orderId);

    //获取单个订单
    @Select("SELECT order_Id, openid, state, total FROM `order` WHERE order_Id = #{orderId}")
    public Order getOrder(String orderId);

}
