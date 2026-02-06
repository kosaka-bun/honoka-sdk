package de.honoka.sdk.spring.starter.security.webflux.token

import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter

object ReactiveJwtUtils {

    val authenticationConverter by lazy {
        newAuthenticationConverter("authorities")
    }

    fun newAuthenticationConverter(
        authoritiesClaimName: String, authorityPrefix: String = ""
    ): ReactiveJwtAuthenticationConverter {
        val authoritiesConverter = JwtGrantedAuthoritiesConverter().apply {
            setAuthoritiesClaimName(authoritiesClaimName)
            setAuthorityPrefix(authorityPrefix)
        }
        val authenticationConverter = ReactiveJwtAuthenticationConverter().apply {
            setJwtGrantedAuthoritiesConverter(
                ReactiveJwtGrantedAuthoritiesConverterAdapter(authoritiesConverter)
            )
        }
        return authenticationConverter
    }
}
