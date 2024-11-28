package de.honoka.sdk.spring.starter.config.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties("honoka.starter")
public class MainProperties {

    @AllArgsConstructor
    @Getter
    public enum Module {
        
        SECURITY
    }
    
    private Module[] enabledModules;
}
