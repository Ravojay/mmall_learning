package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by ravojay on 1/9/20.
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    private IFileService iFileService;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServiceResponse productSave(HttpSession session, Product product){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need to login");
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.saveOrUpdateProduct(product);
        }else{
            return ServiceResponse.createByErrorMsg("need to be admin");
        }
    }


    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServiceResponse set_sal_status(HttpSession session, Integer productId,Integer productStatus){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need to login");
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.setSaleStatus(productId,productStatus);
        }else{
            return ServiceResponse.createByErrorMsg("need to be admin");
        }
    }


    @RequestMapping("detail.do")
    @ResponseBody
    public ServiceResponse getDetail(HttpSession session, Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need to login");
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.manageProductDetail(productId);
        }else{
            return ServiceResponse.createByErrorMsg("need to be admin");
        }
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value="pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need to login");
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.getProductList(pageNum,pageSize);
        }else{
            return ServiceResponse.createByErrorMsg("need to be admin");
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServiceResponse productSearch(HttpSession session,String productName,Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value="pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need to login");
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else{
            return ServiceResponse.createByErrorMsg("need to be admin");
        }
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServiceResponse<Map> upload(HttpSession session, @RequestParam(value = "upload_file",required = false)MultipartFile file, HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"need to login");
        if(!iUserService.checkAdminRole(user).isSuccess()){
            return ServiceResponse.createByErrorMsg("need to be admin");
        }

        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file,path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
        Map fileMap = Maps.newHashMap();
        fileMap.put("uri",targetFileName);
        fileMap.put("url",url);
        return ServiceResponse.createBySuccess(fileMap);
    }

    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false)MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        Map resultMap = Maps.newHashMap();
        if(user==null) {
            resultMap.put("success",false);
            resultMap.put("msg","nedd to login");
            return resultMap;
        }
        if(!iUserService.checkAdminRole(user).isSuccess()){
            resultMap.put("success",false);
            resultMap.put("msg","need to login as admin");
            return resultMap;
        }

        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file,path);
        if(StringUtils.isBlank(targetFileName)){
            resultMap.put("success",false);
            resultMap.put("msg","upload failed");
            return resultMap;
        }
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
        resultMap.put("success",true);
        resultMap.put("msg","success");
        resultMap.put("file_path",url);
        response.addHeader("Access-Control-Allow-Headers","X-File-Name");
        return resultMap;
    }
}
