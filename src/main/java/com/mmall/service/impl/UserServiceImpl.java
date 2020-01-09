package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.Md5Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by ravojay on 1/8/20.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServiceResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount==0) return ServiceResponse.createByErrorMsg("user doesn't exist");

        //todo md5
        String md5Password = Md5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Password);
        if (user == null){
            return ServiceResponse.createByErrorMsg("Incorrect password");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess("Login Success",user);
    }

    public ServiceResponse<String> register(User user){
        int resultCount = userMapper.checkUsername(user.getUsername());
        if(resultCount>0) return ServiceResponse.createByErrorMsg("user already exist");

        resultCount = userMapper.checkEmail(user.getEmail());
        if(resultCount>0) return ServiceResponse.createByErrorMsg("email already exist");

        user.setRole(Const.Role.ROLE_CUSTOMER);

        //md5 encryption
        user.setPassword(Md5Util.MD5EncodeUtf8(user.getPassword()));
        resultCount = userMapper.insert(user);
        if(resultCount==0) return ServiceResponse.createByErrorMsg("failed to create");
        return ServiceResponse.createBySuccessMessage("Success");
    }

    public ServiceResponse<String> checkValid(String str,String type){
        if(StringUtils.isNotBlank(type)){
            if(Const.USERNAME.equals(type)) {
                int a = userMapper.checkUsername(str);
                if (a > 0) return ServiceResponse.createByErrorMsg("user alredy exist");
            }
            if(Const.EMAIL.equals(type)){
                int a = userMapper.checkEmail(str);
                if(a>0) return ServiceResponse.createByErrorMsg("email already exist");
            }
        }else return ServiceResponse.createByErrorMsg("parameter wrong");

        return ServiceResponse.createBySuccess("valid");
    }

    public ServiceResponse selectQuestion(String username){
        ServiceResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()) return ServiceResponse.createByErrorMsg("user not exist");
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)) return ServiceResponse.createBySuccess(question);

        return ServiceResponse.createByErrorMsg("empty question");
    }

    public ServiceResponse<String> chekAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            String forgettoken= UUID.randomUUID().toString();
            TokenCache.setKey("token_"+username,forgettoken);
            return ServiceResponse.createBySuccess(forgettoken);
        }
        return ServiceResponse.createByErrorMsg("answer not correct");
    }

    public ServiceResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
            return ServiceResponse.createByErrorMsg("parameter wrong, token needed");
        }
        ServiceResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()) return ServiceResponse.createByErrorMsg("user not exist");

        String token = TokenCache.getKey("token_"+username);
        if(StringUtils.isBlank(token)) return ServiceResponse.createByErrorMsg("tomen invalid");

        if(StringUtils.equals(forgetToken,token)){
            String md5Password = Md5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if(rowCount>0) return ServiceResponse.createBySuccessMessage("change pssword success");

        }else{
            return ServiceResponse.createByErrorMsg("token wrong please re-gain token");
        }
        return  ServiceResponse.createByErrorMsg("changing password failed");
    }

    public ServiceResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        int count = userMapper.checkPassword(Md5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(count==0) return ServiceResponse.createByErrorMsg("password incorrect");

        user.setPassword(Md5Util.MD5EncodeUtf8(passwordNew));
        int updatecount = userMapper.updateByPrimaryKeySelective(user);
        if(updatecount>0) return ServiceResponse.createBySuccessMessage("changing password success");
        return ServiceResponse.createByErrorMsg("updating failed");
    }

    public ServiceResponse<User> updateInfo(User user){
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount>0) return ServiceResponse.createByErrorMsg("email already exists");
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0) return ServiceResponse.createBySuccess("update successful",updateUser);
        return ServiceResponse.createByErrorMsg("update failed");
    }

    public ServiceResponse<User> getInfo(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user==null){
            return ServiceResponse.createByErrorMsg("user not found");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess(user);
    }

    //check if admin
    public ServiceResponse checkAdminRole(User user){
        if (user!=null&&user.getRole().intValue()==Const.Role.ROLE_ADMIN){
            return ServiceResponse.createBySuccess();
        }else return ServiceResponse.createByError();
    }
}
