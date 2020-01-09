package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Created by ravojay on 1/8/20.
 */
//when being serialized by json, if value is null, key will not be included
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServiceResponse<T> implements Serializable{

    private int status;
    private String msg;
    private T data;

    private ServiceResponse(int status) {
        this.status=status;
    }

    private  ServiceResponse(int status, T data){
        this.status=status;
        this.data=data;
    }

    private  ServiceResponse(int status, String msg,T data){
        this.status=status;
        this.data=data;
        this.msg=msg;
    }

    private  ServiceResponse(int status, String msg){
        this.status=status;
        this.msg=msg;
    }

    @JsonIgnore
    //exclude it from json serialization
    public boolean isSuccess(){
        return this.status==ResponseCode.SUCCESS.getCode();
    }

    public int getStatus(){
        return status;
    }

    public T getData(){
        return data;
    }

    public String getMsg(){
        return msg;
    }

    public static <T> ServiceResponse<T> createBySuccess(){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServiceResponse<T> createBySuccessMessage(String msg){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServiceResponse<T> createBySuccess(T data){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ServiceResponse<T> createBySuccess(String msg,T data){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    public static <T> ServiceResponse<T> createByError(){
        return new ServiceResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    public static <T> ServiceResponse<T> createByErrorMsg(String errMsg){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),errMsg);
    }

    public static <T> ServiceResponse<T> createByErrorCodeMsg(int errCode,String errMsg){
        return new ServiceResponse<T>(errCode,errMsg);
    }
}
