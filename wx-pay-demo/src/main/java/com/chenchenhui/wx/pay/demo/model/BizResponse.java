package com.chenchenhui.wx.pay.demo.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 */
@Data
public class BizResponse {

    // 状态码
    private Integer code;
    // 描述
    private String desc;
    // 数据
    private Object data;


    private final static Map<Integer, String> DESC_BY_CODE = new HashMap<>();

    {
        DESC_BY_CODE.put(0, "成功");

        //微信验证
        //登录
        DESC_BY_CODE.put(2001, "未登录或登录过期,请重新登录");
        DESC_BY_CODE.put(2002, "微信授权失败");
        //用户信息
        DESC_BY_CODE.put(2010, "检验用户信息失败");
        //支付异常
        DESC_BY_CODE.put(2020, "微信支付失败");


    }

    // 成功
    public static BizResponse success(Object data){
        BizResponse br = new BizResponse();
        br.setCode(0);
        br.setDesc(DESC_BY_CODE.get(0));
        br.setData(data);
        return br;
    }

    public static BizResponse success(){
        BizResponse br = new BizResponse();
        br.setCode(0);
        br.setDesc(DESC_BY_CODE.get(0));
        br.setData(null);
        return br;
    }

    // 异常
    public static BizResponse fail(Integer code,Object data){
        BizResponse br = new BizResponse();
        br.setCode(code);
        br.setDesc(DESC_BY_CODE.get(code));
        br.setData(data);
        return br;
    }

    public static BizResponse fail(Integer code){
        BizResponse br = new BizResponse();
        br.setCode(code);
        br.setDesc(DESC_BY_CODE.get(code));
        br.setData(null);
        return br;
    }

}
