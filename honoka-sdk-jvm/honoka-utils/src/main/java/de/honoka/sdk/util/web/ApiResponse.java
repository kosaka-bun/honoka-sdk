package de.honoka.sdk.util.web;

import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class ApiResponse<T> {
    
    private Integer code;
    
    private Boolean success;
    
    private String msg;
    
    private T data;
    
    private ApiResponse() {}
    
    public static <T1> ApiResponse<T1> of() {
        return new ApiResponse<>();
    }
    
    public static <T1> ApiResponse<T1> success(String msg, T1 data) {
        return new ApiResponse<T1>()
            .setCode(HttpStatus.HTTP_OK)
            .setSuccess(true)
            .setMsg(msg)
            .setData(data);
    }
    
    public static <T1> ApiResponse<T1> success(T1 data) {
        return success(null, data);
    }
    
    public static ApiResponse<Object> success() {
        return success(null);
    }
    
    public static ApiResponse<Object> fail(int httpStatus, String msg) {
        return new ApiResponse<>()
            .setCode(httpStatus)
            .setSuccess(false)
            .setMsg(msg)
            .setData(null);
    }
    
    public static ApiResponse<Object> fail(String msg) {
        return fail(HttpStatus.HTTP_INTERNAL_ERROR, msg);
    }
    
    public String toJsonString() {
        return JSONUtil.toJsonStr(this);
    }
}
