package com.chenchenhui.wx.pay.demo.controller;

import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.chenchenhui.wx.pay.demo.config.WxConfig;
import com.chenchenhui.wx.pay.demo.model.BizResponse;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private WxConfig wxConfig;


    /*
    * 用户登录
    * */
    @GetMapping("/login/{code}")
    public BizResponse login(@PathVariable String code) throws WxErrorException {
        //code
        System.out.println("code:{} ===> " + code);
        WxMaUserService userService = wxConfig.getMaService().getUserService();

        System.out.println("userService:{} ===> " + userService.toString());
        //解析
        WxMaJscode2SessionResult session = userService.getSessionInfo(code);

        String openid = session.getOpenid();
        System.out.println("openid:{} ===> " + openid);

        return BizResponse.success(openid);
    }
}
