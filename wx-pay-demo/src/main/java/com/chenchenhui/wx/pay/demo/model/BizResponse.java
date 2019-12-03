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

}
