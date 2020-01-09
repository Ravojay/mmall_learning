package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVO;
import com.mmall.vo.ProductListVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ravojay on 1/9/20.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;

    public ServiceResponse saveOrUpdateProduct(Product product){
        if(product!=null){
            String images = product.getSubImages();
            if(StringUtils.isNotBlank(images)){
                String[] subImgArray = images.split(",");
                if(subImgArray.length>0){
                    product.setMainImage(subImgArray[0]);
                }
            }

            if(product.getId()!=null){
                int rowCount = productMapper.updateByPrimaryKey(product);
                if(rowCount==0) return ServiceResponse.createByErrorMsg("update failed");
                return ServiceResponse.createBySuccess("update success");
            }else{
                int rowCount = productMapper.insert(product);
                if(rowCount==0) return ServiceResponse.createByErrorMsg("insert failed");
                return ServiceResponse.createBySuccess("insert success");
            }
        }
        return ServiceResponse.createByErrorMsg("failed");
    }

    public ServiceResponse<String> setSaleStatus(Integer productId,Integer status){
        if(productId==null||status==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.ILLEEGAL_ARGUMENT.getCode(),"argument wrong");
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount>0) return ServiceResponse.createBySuccess("modification success");
        return ServiceResponse.createByErrorMsg("modification failed");
    }


    public ServiceResponse<ProductDetailVO> manageProductDetail(Integer productId){
        if(productId==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.ILLEEGAL_ARGUMENT.getCode(),"argument wrong");
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product==null) return ServiceResponse.createByErrorMsg("product removed or not exist");
        // Value Object
        ProductDetailVO productDetailVO = assembleProductDetailVO(product);
        return ServiceResponse.createBySuccess(productDetailVO);
    }

    private ProductDetailVO assembleProductDetailVO(Product product){
        ProductDetailVO vo= new ProductDetailVO();
        vo.setId(product.getId());
        vo.setSubtitle(product.getSubtitle());
        vo.setPrice(product.getPrice());
        vo.setMainImage(product.getMainImage());
        vo.setSubIamges(product.getSubImages());
        vo.setCategoryId(product.getCategoryId());
        vo.setDetail(product.getDetail());
        vo.setName(product.getName());
        vo.setStatus(product.getStatus());
        vo.setStock(product.getStock());

        //imagehost
        vo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.mmall.com/"));

        //parent
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category==null){
            vo.setParentCategoryId(0);
        }else{
            vo.setParentCategoryId(category.getParentId());
        }
        //createtim
        vo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        //updatetim
        vo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return vo;
    }

    //mybatis-pagehelper
    public ServiceResponse<PageInfo> getProductList(int pageNum,int pageSize){
        //startpage
        //fill your own sql
        //pagehelper end
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductListVO> plist = Lists.newArrayList();
        for (Product p:productList){
            ProductListVO productListVO = assembleProductListVO(p);
            plist.add(productListVO);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(plist);
        return ServiceResponse.createBySuccess(pageResult);
    }

    private ProductListVO assembleProductListVO(Product product){
        ProductListVO pvo = new ProductListVO();
        pvo.setId(product.getId());
        pvo.setName(product.getName());
        pvo.setCategoryId(product.getCategoryId());
        pvo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.mmall.com/"));
        pvo.setMainImage(product.getMainImage());
        pvo.setPrice(product.getPrice());
        pvo.setSubtitle(product.getSubtitle());
        pvo.setStatus(product.getStatus());
        return pvo;
    }

    public ServiceResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if( StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();

        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);
        List<ProductListVO> plist = Lists.newArrayList();
        for (Product p:productList){
            ProductListVO productListVO = assembleProductListVO(p);
            plist.add(productListVO);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(plist);
        return ServiceResponse.createBySuccess(pageResult);
    }

    public ServiceResponse<ProductDetailVO> getProductDetail(Integer productId){
        if(productId==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.ILLEEGAL_ARGUMENT.getCode(),"argument wrong");
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product==null) return ServiceResponse.createByErrorMsg("product removed or not exist");
        if(product.getStatus()!= Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServiceResponse.createByErrorMsg("product removed or not exist");
        }
        // Value Object
        ProductDetailVO productDetailVO = assembleProductDetailVO(product);
        return ServiceResponse.createBySuccess(productDetailVO);
    }


    public ServiceResponse<PageInfo> getProductByKeywordCategory(String keyword,
                                                                 Integer categoryId,int pageNum,
                                                                 int pageSize, String orderBy){
        if(StringUtils.isBlank(keyword)&&categoryId==null) return ServiceResponse.createByErrorCodeMsg(ResponseCode.ILLEEGAL_ARGUMENT.getCode(),"argument wrong");
        List<Integer> categoryIdList = new ArrayList<>();
        if(categoryId!=null){
            Category cat = categoryMapper.selectByPrimaryKey(categoryId);
            if(cat==null&&StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVO> psvo = Lists.newArrayList();
                PageInfo pageInfo=new PageInfo<>(psvo);
                return ServiceResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword=  new StringBuilder().append("%").append(keyword).append("%").toString();

        }

        PageHelper.startPage(pageNum,pageSize);
        //sort
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] arr = orderBy.split("_");
                PageHelper.orderBy(arr[0]+" "+arr[1]);
            }
        }
        List<Product> productList=productMapper.selectByNameAndCategoryIds( StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);
        List<ProductListVO> psvo = Lists.newArrayList();
        for(Product p:productList){
            ProductListVO temp = assembleProductListVO(p);
            psvo.add(temp);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(psvo);
        return ServiceResponse.createBySuccess(pageInfo);

    }
}
