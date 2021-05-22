package com.naown.common.lang;

import org.springframework.http.HttpStatus;

import java.util.HashMap;

/**
 * @author: chenjian
 * @since: 2021/5/9 23:04 周日
 **/
public class Result extends HashMap<String,Object> {
    public Result(){
        put("code", HttpStatus.OK.value());
        put("msg", "操作成功");
    }

    /**
     * 成功
     * @param data 需要返回的数据
     * @return
     */
    public static Result succeed(Object data) {
        Result result = new Result();
        result.put("data",data);
        return result;
    }
    /**
     * 成功
     * @param msg 需要返回的消息
     * @return
     */
    public static Result succeed(String msg) {
        Result result = new Result();
        result.put("meg",msg);
        return result;
    }

    /**
     * 成功
     * @param data 需要返回的数据
     * @param msg 需要返回的消息
     * @return
     */
    public static Result succeed(Object data,String msg) {
        Result result = new Result();
        result.put("data",data);
        result.put("msg",msg);
        return result;
    }

    /**
     * 失败
     * @param code 返回的code码
     * @param msg 错误消息
     * @return
     */
    public static Result error(Integer code, String msg) {
        Result result = new Result();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }

    /**
     * 自定义异常
     * @param msg 错误消息
     * @return
     */
    public static Result error(String msg) {
        return error(500, msg);
    }

    @Override
    public Result put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
