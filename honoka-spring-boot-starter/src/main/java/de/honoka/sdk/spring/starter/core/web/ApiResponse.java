package de.honoka.sdk.spring.starter.core.web;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Data
@Accessors(chain = true)
public class ApiResponse<T> {
    
    private Integer code;
    
    private Boolean status;
    
    private String msg;
    
    private T data;
    
    private ApiResponse() {}
    
    public static <T1> ApiResponse<T1> of() {
        return new ApiResponse<>();
    }
    
    public static <T1> ApiResponse<T1> success(String msg, T1 data) {
        return new ApiResponse<T1>()
            .setCode(HttpStatus.OK.value())
            .setStatus(true)
            .setMsg(msg)
            .setData(data);
    }
    
    public static <T1> ApiResponse<T1> success(T1 data) {
        return success(null, data);
    }
    
    public static ApiResponse<?> success() {
        return success(null);
    }
    
    public static ApiResponse<?> fail(int httpStatus, String msg) {
        return new ApiResponse<>()
            .setCode(httpStatus)
            .setStatus(false)
            .setMsg(msg)
            .setData(null);
    }
    
    public static ApiResponse<?> fail(String msg) {
        return fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }
    
    public String toJsonString() {
        return JSONUtil.toJsonStr(this);
    }
}
