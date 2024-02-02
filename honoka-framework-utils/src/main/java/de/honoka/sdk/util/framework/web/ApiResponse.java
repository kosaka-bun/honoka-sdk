package de.honoka.sdk.util.framework.web;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.servlet.http.HttpServletResponse;

@Data
@Accessors(chain = true)
public class ApiResponse<T> {

    private Integer code;

    private Boolean status;

    private String msg;

    private T data;

    public static <T1> ApiResponse<T1> success(String msg, T1 data) {
        return new ApiResponse<T1>()
                .setCode(HttpServletResponse.SC_OK)
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
        return fail(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
    }
}
