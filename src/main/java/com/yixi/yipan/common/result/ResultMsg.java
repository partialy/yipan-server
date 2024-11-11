package com.yixi.yipan.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultMsg<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> ResultMsg<T> success(T data) {
        return new ResultMsg<>(200, "success", data);
    }

    public static <T> ResultMsg<T> error(String message) {
        return new ResultMsg<>(400, message, null);
    }
}
