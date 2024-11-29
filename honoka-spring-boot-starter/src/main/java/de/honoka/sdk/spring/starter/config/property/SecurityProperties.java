package de.honoka.sdk.spring.starter.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(SecurityProperties.PREFIX)
public class SecurityProperties {
    
    public static final String PREFIX = MainProperties.PREFIX + ".security";
    
    private boolean enabled = false;
    
    private String jwtKey = "abcde12345";
    
    private String[] whiteList = {};
    
    private String[] corsOrigins = {};
}
