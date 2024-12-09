package de.honoka.sdk.spring.starter.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(SecurityProperties.PREFIX)
@Data
public class SecurityProperties {
    
    public static final String PREFIX = MainProperties.PREFIX + ".security";
    
    private boolean enabled = false;
    
    private String[] whiteList = {};
    
    private String[] corsOrigins = {};
    
    private Token token = new Token();
    
    @Data
    public static class Token {
        
        private String jwtKey = "abcde12345";
        
        private String name = "token";
        
        private String tempName = "temp-token";
    }
}
