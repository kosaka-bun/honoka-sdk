package de.honoka.sdk.spring.starter.core

import de.honoka.sdk.spring.starter.config.MainConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component("${MainConfig.STARTER_BEAN_NAME_PREFIX}SpringPropertiesHolder")
class SpringPropertiesHolder {

    @Value($$"${spring.application.name:#{null}}")
    var applicationName: String? = null

    @Value($$"${spring.datasource.driver-class-name:#{null}}")
    var jdbcDriverClassName: String? = null
}
