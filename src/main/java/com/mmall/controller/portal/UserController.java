package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpSession;

/**
 * Created by ravojay on 1/8/20.
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /*
    login
    @param username
    @param password
    @param session
    * */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> login(String username, String password, HttpSession session){
        //service -->batis->dao
        ServiceResponse<User> response = iUserService.login(username,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServiceResponse.createBySuccess();
    }

    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> register(User user){
        return iUserService.register(user);
    }

    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user!=null) return ServiceResponse.createBySuccess(user);
        return ServiceResponse.createByErrorMsg("user not login");
    }

    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.chekAnswer(username,question,answer);
    }

    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }

    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorMsg("user not signed in");
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> updateInfo(HttpSession session, User user){
        User cuser = (User) session.getAttribute(Const.CURRENT_USER);
        if(cuser==null) return ServiceResponse.createByErrorMsg("user not signed in");
        user.setId(cuser.getId());
        user.setUsername(cuser.getUsername());
        ServiceResponse<User> response = iUserService.updateInfo(user);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> getInfo(HttpSession session){
        User cuser = (User) session.getAttribute(Const.CURRENT_USER);
        if(cuser==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"not signed in yet");
        return iUserService.getInfo(cuser.getId());
    }
}
