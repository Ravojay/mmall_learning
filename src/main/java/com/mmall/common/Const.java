package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by ravojay on 1/8/20.
 */
public class Const {
    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }
    public static final String CURRENT_USER="crrentUser";
    public static final String EMAIL="email";
    public static final String USERNAME = "username";
    public interface Role{
        int ROLE_CUSTOMER=0;
        int ROLE_ADMIN=1;
    }

    public enum ProductStatusEnum{
        ON_SALE(1,"online");
        private String value;
        private int code;

        ProductStatusEnum(int code,String value){
            this.code=code;
            this.value=value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }
}
