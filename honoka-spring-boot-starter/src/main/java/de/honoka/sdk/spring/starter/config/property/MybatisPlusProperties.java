package de.honoka.sdk.spring.starter.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(MybatisPlusProperties.PREFIX)
@Data
public class MybatisPlusProperties {
    
    public static final String PREFIX = MainProperties.PREFIX + ".mybatis";
    
    private boolean enabled = false;
}
