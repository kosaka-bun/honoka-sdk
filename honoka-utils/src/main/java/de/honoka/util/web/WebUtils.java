package de.honoka.util.web;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Web相关工具
 */
public class WebUtils {

    /**
     * 从请求中获取请求方的真实IP，忽略反向代理
     */
    public static String getRealIp(HttpServletRequest request) {
        //反向代理后：转发请求的HTTP头信息中，增加了X-Real-IP信息
        String ip = request.getHeader("X-Real-IP");
        final String STR_UNKNOWN = "unknown";
        if(StringUtils.isBlank(ip) || STR_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if(StringUtils.isBlank(ip) || STR_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(StringUtils.isBlank(ip) || STR_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(StringUtils.isBlank(ip) || STR_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if(ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}
