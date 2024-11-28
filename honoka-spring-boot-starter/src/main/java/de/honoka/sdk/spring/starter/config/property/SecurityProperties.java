package de.honoka.sdk.spring.starter.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties("honoka.starter.security")
public class SecurityProperties {
    
    private String jwtKey = "abcde12345";
    
    private String[] whiteList;
    
    private String[] corsOrigins;
}
