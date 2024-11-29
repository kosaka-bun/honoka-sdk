package de.honoka.sdk.spring.starter.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(MainProperties.PREFIX)
public class MainProperties {
    
    public static final String PREFIX = "honoka.starter";
}
