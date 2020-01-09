package com.mmall.util;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.security.MessageDigest;
import java.util.Properties;

/**
 * Created by ravojay on 1/8/20.
 */
public class Md5Util {

    private static final String hexDigits[]={"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};

    private static String byteArrayToHexString(byte b[]){
        StringBuffer res = new StringBuffer();
        for (int i=0;i<b.length;i++) res.append(byteToHexString(b[i]));
        return res.toString();
    }

    private static String byteToHexString(byte b){
        int n=b;
        if(n<0) n+=256;
        int d1=n/16;
        int d2=n%16;
        return hexDigits[d1]+hexDigits[d2];
    }

    private static String MD5Encod(String origin,String charset){
        String res = null;
        try{
            res = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if(charset==null||"".equals(charset)) res = byteArrayToHexString(md.digest(res.getBytes()));
            else res = byteArrayToHexString(md.digest(res.getBytes(charset)));
        }catch (Exception e){}
        return res;
    }

    public static String MD5EncodeUtf8(String origin) {
        return MD5Encod(origin,"utf-8");
    }
}
