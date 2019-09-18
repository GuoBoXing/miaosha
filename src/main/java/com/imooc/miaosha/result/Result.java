package com.imooc.miaosha.result;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    private Result(T data){
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    private Result(CodeMsg cm) {
        if (cm == null) {
            return;
        }
        this.code = cm.getCode();
        this.msg = cm.getMsg();
    }

    /***
     * 成功的时候调用
     * @param <T>
     * @return
     */
    public static<T> Result<T> success(T data) {
        return new Result<T>(data);
    }
    /***
     * 失败的时候调用
     * @param <T>
     * @return
     */
    public static<T> Result<T> error(CodeMsg cm) {
        return new Result<T>(cm);
    }
}
