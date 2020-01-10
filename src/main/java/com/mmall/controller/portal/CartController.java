package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by ravojay on 1/9/20.
 */
@Controller
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    ICartService iCartService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServiceResponse<CartVO> add(HttpSession session, Integer count, Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need login");
        return iCartService.add(user.getId(),productId,count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServiceResponse<CartVO> update(HttpSession session, Integer count, Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need login");
        return iCartService.update(user.getId(),productId,count);
    }

    @RequestMapping("delete.do")
    @ResponseBody
    public ServiceResponse<CartVO> delete(HttpSession session, String productIds){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need login");
        return iCartService.delete(user.getId(),productIds);
    }


    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse<CartVO> list(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need login");
        return iCartService.list(user.getId());
    }


    @RequestMapping("select_all.do")
    @ResponseBody
    public ServiceResponse<CartVO> selectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need login");
        return iCartService.selectOrUnselect(user.getId(),Const.Cart.CHECKED,null);
    }


    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServiceResponse<CartVO> unSelectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need login");
        return iCartService.selectOrUnselect(user.getId(),Const.Cart.UN_CHECKED,null);
    }


    @RequestMapping("select.do")
    @ResponseBody
    public ServiceResponse<CartVO> select(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need login");
        return iCartService.selectOrUnselect(user.getId(),Const.Cart.CHECKED,productId);
    }


    @RequestMapping("un_select.do")
    @ResponseBody
    public ServiceResponse<CartVO> unSelect(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need login");
        return iCartService.selectOrUnselect(user.getId(),Const.Cart.UN_CHECKED,productId);
    }

    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServiceResponse<Integer> getCartProductCount(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createBySuccess(0);
        return iCartService.getCartProductCount(user.getId());
    }


}
