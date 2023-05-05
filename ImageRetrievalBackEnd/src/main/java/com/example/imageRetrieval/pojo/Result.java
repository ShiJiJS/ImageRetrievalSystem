package com.example.imageRetrieval.pojo;

import lombok.Data;

/**
 * @Author ShiJi
 * @create 2023/3/10 10:52
 */
@Data
public class Result {
    private Object data;
    private Integer code;
    private String msg;

    public Result(Object data, Integer code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public Result(Object data, Integer code) {
        this.data = data;
        this.code = code;
    }

}
