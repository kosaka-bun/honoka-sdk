package de.honoka.sdk.spring.starter.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

open class DefaultUser {
    
    var id: Long? = null
    
    var username: String? = null
    
    var password: String? = null
    
    var authorities: String? = null
    
    var enabled: Boolean? = null
    
    var locked: Boolean? = null
    
    var expireTime: Date? = null
    
    var credentialsExpireTime: Date? = null
    
    fun toUserDetails(): DefaultUserDetails = DefaultUserDetails(this)
}

@Suppress("MemberVisibilityCanBePrivate")
open class DefaultUserDetails(private val user: DefaultUser) : UserDetails {
    
    val authorityList: List<String>?
        get() = user.authorities?.split(",")
    
    val authorityObjects: List<GrantedAuthority>?
        get() = authorityList?.map { SimpleGrantedAuthority(it) }
    
    override fun getUsername(): String? = user.username
    
    override fun getPassword(): String? = user.password
    
    override fun getAuthorities(): MutableCollection<out GrantedAuthority>? = authorityObjects?.toMutableList()
    
    override fun isEnabled(): Boolean = user.enabled == true
    
    override fun isAccountNonLocked(): Boolean = user.locked == false
    
    override fun isAccountNonExpired(): Boolean {
        return System.currentTimeMillis() < (user.expireTime?.time ?: Long.MAX_VALUE)
    }
    
    override fun isCredentialsNonExpired(): Boolean {
        return System.currentTimeMillis() < (user.credentialsExpireTime?.time ?: Long.MAX_VALUE)
    }
}