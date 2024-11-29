package de.honoka.sdk.spring.starter.config

import de.honoka.sdk.spring.starter.config.property.SecurityProperties
import de.honoka.sdk.spring.starter.security.AccessDeniedHandlerImpl
import de.honoka.sdk.spring.starter.security.AuthenticationEntryPointImpl
import de.honoka.sdk.spring.starter.security.DefaultAuthorizationFilter
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.AuthorizationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@EnableMethodSecurity
@EnableWebSecurity
@ComponentScan("de.honoka.sdk.spring.starter.security")
@EnableConfigurationProperties(SecurityProperties::class)
@ConditionalOnProperty(prefix = SecurityProperties.PREFIX, name = ["enabled"])
@Configuration
class SecurityConfig(private val securityProperties: SecurityProperties) {
    
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http.run {
        val whiteList = arrayOf(*securityProperties.whiteList, "/error")
        cors {
            it.configurationSource(UrlBasedCorsConfigurationSource().apply {
                registerCorsConfiguration("/**", CorsConfiguration().apply {
                    allowCredentials = true
                    addAllowedHeader("*")
                    addAllowedMethod("*")
                    securityProperties.corsOrigins.forEach { s ->
                        //必须以域名结尾，包含带端口和不带端口两种情况
                        addAllowedOriginPattern("*://*$s")
                        addAllowedOriginPattern("*://*$s:*")
                    }
                })
            })
        }
        csrf {
            /*
             * CSRF攻击防护仅在由服务端从Cookie中取得token时有意义，若服务端不从请求方提供的Cookie中
             * 获取token，那么可以禁用CSRF防护。
             * 此外，若所有接口都接受JSON格式的参数，那么通过在网页上嵌入form表单的方式来发起的POST
             * 请求不会被后端处理，但通过fetch或ajax可以发起跨站请求，需验证这种请求是否会携带目标站点
             * 的Cookie。
             */
            it.disable()
        }
        //添加能够识别自定义登录态，并将其放入SecurityContextHolder中的处理器
        addFilterBefore(DefaultAuthorizationFilter, AuthorizationFilter::class.java)
        logout {
            it.disable()
        }
        //设置自定义的Security异常处理器，用于处理未登录、无权访问等情况
        exceptionHandling {
            /*
             * 定义认证入口点，当AuthorizationFilter检测到SecurityContextHolder的context中没有
             * authentication信息时，则调用此处配置的authenticationEntryPoint中的方法，来执行开发者
             * 定义的后续行为。
             */
            it.authenticationEntryPoint(AuthenticationEntryPointImpl)
            it.accessDeniedHandler(AccessDeniedHandlerImpl)
        }
        authorizeHttpRequests {
            val whiteListMatchers = whiteList.map { s -> AntPathRequestMatcher.antMatcher(s) }.toTypedArray()
            it.requestMatchers(*whiteListMatchers).permitAll()
            it.anyRequest().authenticated()
        }
        build()
    }
    
    //不使用UserDetailsService，为了防止Spring Security自动在控制台生成临时密码，使用此默认配置
    @Bean
    fun userDetailService(): UserDetailsService = InMemoryUserDetailsManager()
}