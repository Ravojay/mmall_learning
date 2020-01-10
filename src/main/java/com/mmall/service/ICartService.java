package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.vo.CartVO;
import org.springframework.stereotype.Service;

/**
 * Created by ravojay on 1/9/20.
 */
@Service("iCartService")
public interface ICartService{
    ServiceResponse<CartVO> add(Integer userId, Integer productId, Integer count);
    ServiceResponse<CartVO> update (Integer userId,Integer productId,Integer count);
    ServiceResponse<CartVO> delete(Integer userId,String productIds);
    ServiceResponse<CartVO> list(Integer userId);
    ServiceResponse<Integer> getCartProductCount(Integer userId);
    ServiceResponse<CartVO> selectOrUnselect(Integer userId,Integer checked,Integer productId);
}
