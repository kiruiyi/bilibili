package com.domin;

import lombok.Data;

@Data
public class R<T> {

    private String code;
    private String msg;
    private T data;


    public static R<String> fail(String code, String msg) {
        return new R<>(code, msg);
    }


    //TODO 不带数据的返回  失败
    public static R<String> fail() {
        return new R<>("1", "失败");
    }

    //TODO 带数据的返回  成功
    public static <T> R<T> success(T data) {
        return new R<>(data);
    }

    //TODO 不带数据的返回  成功
    public static R<String> success() {
        return new R<>(null);
    }



    public R(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(T data) {
        this.data = data;
        msg = "成功";
        code = "0";
    }


}
