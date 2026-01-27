package de.honoka.sdk.spring.starter.core

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SpringPropertiesHolder {

    @Value($$"${server.port:#{null}}")
    var serverPort: Int? = null

    @Value($$"${spring.application.name:#{null}}")
    var applicationName: String? = null

    @Value($$"${spring.datasource.driver-class-name:#{null}}")
    var jdbcDriverClassName: String? = null
}
