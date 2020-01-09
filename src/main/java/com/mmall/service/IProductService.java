package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVO;
import org.springframework.stereotype.Service;

/**
 * Created by ravojay on 1/9/20.
 */

public interface IProductService {
    ServiceResponse saveOrUpdateProduct(Product product);
    ServiceResponse<String> setSaleStatus(Integer productId,Integer status);
    ServiceResponse<ProductDetailVO> manageProductDetail(Integer productId);
    ServiceResponse<PageInfo> getProductList(int pageNum, int pageSize);
    ServiceResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum, int pageSize);
    ServiceResponse<ProductDetailVO> getProductDetail(Integer productId);
    ServiceResponse<PageInfo> getProductByKeywordCategory(String keyword,
                                                          Integer categoryId,int pageNum,
                                                          int pageSize, String orderBy);
}
