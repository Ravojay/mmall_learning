package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;

import javax.servlet.http.HttpSession;

/**
 * Created by ravojay on 1/8/20.
 */
public interface IUserService {
    ServiceResponse<User> login(String username, String password);
    ServiceResponse<String> register(User user);
    ServiceResponse<String> checkValid(String str,String type);
    ServiceResponse selectQuestion(String username);
    ServiceResponse<String> chekAnswer(String username,String question,String answer);
    ServiceResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);
    ServiceResponse<String> resetPassword(String passwordOld,String passwordNew,User user);
    ServiceResponse<User> updateInfo(User user);
    ServiceResponse<User> getInfo(Integer userId);
    ServiceResponse checkAdminRole(User user);
}
