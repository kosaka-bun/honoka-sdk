package de.honoka.sdk.spring.starter.security

import de.honoka.sdk.util.kotlin.lang.AllOpen
import de.honoka.sdk.util.kotlin.text.toJsonArray
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@AllOpen
class DefaultUser {
    
    var id: Long? = null
    
    var username: String? = null
    
    var password: String? = null
    
    var authorities: String? = null
    
    var enabled: Boolean? = null
    
    var locked: Boolean? = null
    
    var expireTime: Date? = null
    
    var credentialsExpireTime: Date? = null
}

open class DefaultUserDetails(private val user: DefaultUser) : UserDetails {
    
    override fun getUsername(): String = user.username!!
    
    override fun getPassword(): String = user.password!!
    
    override fun getAuthorities(): Collection<GrantedAuthority> =
        user.authorities!!.toJsonArray().map { SimpleGrantedAuthority(it as String) }
    
    override fun isEnabled(): Boolean = user.enabled != false
    
    override fun isAccountNonLocked(): Boolean = user.locked != true
    
    override fun isAccountNonExpired(): Boolean = user.expireTime.let {
        if(it != null) System.currentTimeMillis() <= it.time else true
    }

    override fun isCredentialsNonExpired(): Boolean = user.credentialsExpireTime.let {
        if(it != null) System.currentTimeMillis() <= it.time else true
    }
}

fun DefaultUser.toUserDetails(): DefaultUserDetails = DefaultUserDetails(this)
