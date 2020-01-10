package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by ravojay on 1/10/20.
 */
@Controller
@RequestMapping("/shipping/")
public class ShippingController {
    @Autowired
    private IShippingService iShippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServiceResponse add(HttpSession session, Shipping shipping){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need login");
        return iShippingService.add(user.getId(),shipping);
    }


    @RequestMapping("del.do")
    @ResponseBody
    public ServiceResponse del(HttpSession session, Integer shippingId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need login");
        return iShippingService.del(user.getId(),shippingId);
    }


    @RequestMapping("update.do")
    @ResponseBody
    public ServiceResponse update(HttpSession session, Shipping shipping){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need login");
        return iShippingService.update(user.getId(),shipping);
    }


    @RequestMapping("select.do")
    @ResponseBody
    public ServiceResponse<Shipping> select(HttpSession session, Integer shippingId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need login");
        return iShippingService.select(user.getId(),shippingId);
    }


    public ServiceResponse<L<Shipping>>
}
