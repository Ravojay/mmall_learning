package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    List<Cart> selectCartByUserId(Integer userId);

    int selectCartProductCheckedStatusByUserId(Integer userId);

    int checkOrUncheckProduct(@Param("userId") Integer userId,@Param("checked") Integer checked,@Param("productId") Integer productId);

    int selectCartProductCount(Integer userId);

    int deleteByUserIdAndProductIds(@Param("userId") Integer userId,@Param("productIdList") List<String> productIdList);

    Cart selectCartByUserIdProductId(@Param("userId") Integer userId,@Param("productId") Integer productId);
}