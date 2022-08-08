package com.example.autooccupation.bean;

import java.time.LocalDateTime;

/**
 * @Author iCHuang
 * @Date 2022/4/16 11:28
 */
public class Result {
    private Integer code;
    private String msg;
    private Object data;

    public static Result suss(String msg) {
        Result result = new Result();
        result.setCode(200);
        result.setData(null);
        result.setMsg(msg);
        return  result;
    }
    public static Result suss(Object data,String msg) {
        Result result = new Result();
        result.setCode(200);
        result.setData(data);
        result.setMsg(msg);
        return  result;
    }

    public static Result fail(String msg) {
        Result result = new Result();
        result.setCode(400);
        result.setData(null);
        result.setMsg(msg);
        return  result;
    }



    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
