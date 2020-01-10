package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVO;
import com.mmall.vo.CartVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by ravojay on 1/9/20.
 */
public class CartServiceImpl implements ICartService{
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;


    public ServiceResponse<CartVO> add(Integer userId,Integer productId,Integer count){

        if(productId==null||count==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.ILLEEGAL_ARGUMENT.getCode(),"argument wrong");

        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart==null) {
            Cart cartItem = new Cart();
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setQuantity(count);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);

            cartMapper.insert(cartItem);
        }else{
            count = cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }

        CartVO cartVO = getCartVOLimit(userId);
        return ServiceResponse.createBySuccess(cartVO);
    }


    private CartVO getCartVOLimit(Integer userId){
        CartVO cartVO = new CartVO();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVO> cartProductVOList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");

        if(CollectionUtils.isNotEmpty(cartList)){
            for(Cart c:cartList){
                CartProductVO cartProductVO = new CartProductVO();
                cartProductVO.setId(c.getId());
                cartProductVO.setUserId(userId);
                cartProductVO.setProductId(c.getProductId());

                Product product = productMapper.selectByPrimaryKey(c.getProductId());
                if(product!=null){
                    cartProductVO.setProductMainImage(product.getMainImage());
                    cartProductVO.setProductName(product.getName());
                    cartProductVO.setProductSubtitle(product.getSubtitle());
                    cartProductVO.setProductStatus(product.getStatus());
                    cartProductVO.setProductPrice(product.getPrice());
                    cartProductVO.setProductStock(product.getStock());

                    int buyLimit=0;
                    if(product.getStock()>=c.getQuantity()){
                        buyLimit = c.getQuantity();
                        cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimit = product.getStock();
                        cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                //update cart to the current valid stock
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(c.getId());
                        cartForQuantity.setQuantity(buyLimit);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }

                    cartProductVO.setQuantity(buyLimit);
                    //calculate totalPrice for one item
                    cartProductVO.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVO.getQuantity()));
                    cartProductVO.setProductChecked(c.getChecked());
                }

                if(c.getChecked()==Const.Cart.CHECKED){
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVO.getProductTotalPrice().doubleValue());
                }

                cartProductVOList.add(cartProductVO);
            }
        }

        cartVO.setCartTotalPrice(cartTotalPrice);
        cartVO.setCartProductVOList(cartProductVOList);
        cartVO.setAllChecked(getAllCheckedStatus(userId));
        cartVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVO;
    }

    private boolean getAllCheckedStatus(Integer userId){
        if(userId==null) return false;
        return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0;
    }


    public ServiceResponse<CartVO> update (Integer userId,Integer productId,Integer count){
        if(productId==null||count==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.ILLEEGAL_ARGUMENT.getCode(),"argument wrong");
        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart!=null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        CartVO cartVO = this.getCartVOLimit(userId);
        return ServiceResponse.createBySuccess(cartVO);

    }


    public ServiceResponse<CartVO> delete(Integer userId,String productIds){
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)) return ServiceResponse.createByErrorCodeMsg(ResponseCode.ILLEEGAL_ARGUMENT.getCode(),"argument wrong");
        cartMapper.deleteByUserIdAndProductIds(userId,productList);
        CartVO cartVO = this.getCartVOLimit(userId);
        return ServiceResponse.createBySuccess(cartVO);
    }

    public ServiceResponse<CartVO> list(Integer userId){
        CartVO cartVO = this.getCartVOLimit(userId);
        return ServiceResponse.createBySuccess(cartVO);
    }

    public ServiceResponse<CartVO> selectOrUnselect(Integer userId,Integer checked,Integer productId){
        cartMapper.checkOrUncheckProduct(userId,checked,productId);
        this.list(userId);
    }

    public ServiceResponse<Integer> getCartProductCount(Integer userId){
        if(userId==null) return ServiceResponse.createBySuccess(0);
        return ServiceResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

}
