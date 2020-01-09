package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by ravojay on 1/8/20.
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;


    @RequestMapping("add_category.do")
    @ResponseBody
    public ServiceResponse addCategory(HttpSession session, String categoryName, @RequestParam(value="parentId",defaultValue = "0") int parentId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"user not signed in");
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.addCategory(categoryName,parentId);
        }else return ServiceResponse.createByErrorMsg("need admin previledge");
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServiceResponse setCategoryName(HttpSession session,Integer categoryId,String categoryName){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"user not signed in");
        if(iUserService.checkAdminRole(user).isSuccess()){
            //update
            return iCategoryService.updateCatedoryName(categoryId,categoryName);
        }else return ServiceResponse.createByErrorMsg("need admin previledge");
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServiceResponse getChildrenParallel(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"user not signed in");
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else return ServiceResponse.createByErrorMsg("need admin previledge");
    }


    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServiceResponse getCategoryAndDeep(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"user not signed in");
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else return ServiceResponse.createByErrorMsg("need admin previledge");
    }
}
