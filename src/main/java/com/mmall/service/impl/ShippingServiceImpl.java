package com.mmall.service.impl;

import com.google.common.collect.Maps;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by ravojay on 1/10/20.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService{
    @Autowired
    private ShippingMapper shippingMapper;

    public ServiceResponse add(Integer userID, Shipping shipping){
        shipping.setUserId(userID);
        int rowCount = shippingMapper.insertSelective(shipping);
        if(rowCount>0){
            Map result = Maps.newHashMap();
            result.put("shippingIrd",shipping.getId());
            return ServiceResponse.createBySuccess("add success",result) ;
        }
        return ServiceResponse.createByErrorMsg("add failed");
    }


    public ServiceResponse<String> del(Integer userId,Integer shippingId){
        int resCount = shippingMapper.deleteByShippingIdAndUserId(userId,shippingId);
        if(resCount>0) return ServiceResponse.createBySuccess("delete success");
        return ServiceResponse.createByErrorMsg("delete failed");
    }


    public ServiceResponse update(Integer userID, Shipping shipping){
        shipping.setUserId(userID);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if(rowCount>0){
            Map result = Maps.newHashMap();
            result.put("shippingIrd",shipping.getId());
            return ServiceResponse.createBySuccess("update success") ;
        }
        return ServiceResponse.createByErrorMsg("update failed");
    }

    public ServiceResponse<Shipping> select(Integer userId,Integer shippingId){
        Shipping shipping = shippingMapper.selectByShippingIdAndUserId(userId,shippingId);
        if(shipping!=null) return ServiceResponse.createBySuccess("select success",shipping);
        return ServiceResponse.createByErrorMsg("select failed");
    }
}
