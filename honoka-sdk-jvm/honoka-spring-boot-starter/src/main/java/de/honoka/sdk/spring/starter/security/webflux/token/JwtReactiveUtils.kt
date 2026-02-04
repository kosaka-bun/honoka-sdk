package de.honoka.sdk.spring.starter.security.webflux.token

import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter

object JwtReactiveUtils {

    fun newJwtAuthenticationConverter(
        authoritiesClaimName: String = "authorities", authorityPrefix: String = ""
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
