package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by ravojay on 1/8/20.
 */
public class TokenCache {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TokenCache.class);
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
        @Override
        public String load(String key) throws Exception {
            return "null";
        }
    });

    public static void setKey(String key,String vlaue){
        localCache.put(key,vlaue);
    }

    public static String getKey(String key){
        String value=null;
        try{
            value = localCache.get(key);
            if("null".equals(value)) return null;
            return value;
        }catch (Exception e){
            logger.error("local cache get error",e);
        }
        return null;
    }
}
