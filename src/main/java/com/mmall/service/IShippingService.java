package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Shipping;

/**
 * Created by ravojay on 1/10/20.
 */
public interface IShippingService {
    ServiceResponse add(Integer userID, Shipping shipping);
    ServiceResponse<String> del(Integer userId,Integer shippingId);
    ServiceResponse update(Integer userID, Shipping shipping);
    ServiceResponse<Shipping> select(Integer userId,Integer shippingId);
}
