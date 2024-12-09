package de.honoka.sdk.spring.starter.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(MainProperties.PREFIX)
@Data
public class MainProperties {
    
    public static final String PREFIX = "honoka.starter";
}
