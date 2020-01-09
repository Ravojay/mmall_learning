package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ravojay on 1/8/20.
 */
@Service("iCategoryService")
public class CategoryServiceImplment implements ICategoryService{
    @Autowired
    private CategoryMapper categoryMapper;

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImplment.class);

    public ServiceResponse addCategory(String categoryName,Integer parentId){
        if(parentId==null|| StringUtils.isBlank(categoryName)){
            return ServiceResponse.createByErrorMsg("parameter wrong");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);

        int rowCount=categoryMapper.insert(category);
        if(rowCount>0) return ServiceResponse.createBySuccess("creating success");
        return ServiceResponse.createByErrorMsg("creating failed");
    }


    public ServiceResponse updateCatedoryName(Integer categoryId,String categoryName){
        if(categoryId==null|| StringUtils.isBlank(categoryName)){
            return ServiceResponse.createByErrorMsg("parameter wrong");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int roeCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(roeCount>0) return ServiceResponse.createBySuccess("update success");
        return ServiceResponse.createByErrorMsg("update dailed");
    }

    public ServiceResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.info("chidren not found");
        }
        return ServiceResponse.createBySuccess(categoryList);
    }


    public ServiceResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);
        List<Integer> categoryList = Lists.newArrayList();
        if(categoryId!=null){
            for(Category c:categorySet) categoryList.add(c.getId());
        }
        return ServiceResponse.createBySuccess(categoryList);
    }

    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null){
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category c:categoryList){
            findChildCategory(categorySet,c.getId());
        }
        return categorySet;
    }
}
