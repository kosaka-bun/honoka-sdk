package de.honoka.sdk.spring.starter.security

import de.honoka.sdk.util.kotlin.text.toJsonArray
import de.honoka.sdk.util.kotlin.various.AllOpen
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@AllOpen
class DefaultUser {
    
    var id: Long? = null
    
    var username: String? = null
    
    var password: String? = null

    var roles: String? = null
    
    var authorities: String? = null
    
    var enabled: Boolean? = null
    
    var locked: Boolean? = null
    
    var expireTime: Date? = null
    
    var credentialsExpireTime: Date? = null
}

open class DefaultUserDetails(private val user: DefaultUser) : UserDetails {
    
    override fun getUsername(): String = user.username!!
    
    override fun getPassword(): String = user.password!!
    
    override fun getAuthorities(): Collection<GrantedAuthority> = user.springAuthorityObjects

    override fun isEnabled(): Boolean = user.enabled != false
    
    override fun isAccountNonLocked(): Boolean = user.locked != true
    
    override fun isAccountNonExpired(): Boolean = user.expireTime.let {
        if(it != null) System.currentTimeMillis() <= it.time else true
    }

    override fun isCredentialsNonExpired(): Boolean = user.credentialsExpireTime.let {
        if(it != null) System.currentTimeMillis() <= it.time else true
    }
}

val DefaultUser.springAuthorities: List<String>
    get() {
        val list = ArrayList<String>().apply {
            roles?.toJsonArray()?.forEach { r ->
                add("ROLE_$r")
            }
            authorities?.toJsonArray()?.let { j ->
                @Suppress("UNCHECKED_CAST")
                addAll(j as List<String>)
            }
        }
        return list
    }

val DefaultUser.springAuthorityObjects: List<GrantedAuthority>
    get() = springAuthorities.map { SimpleGrantedAuthority(it) }

fun DefaultUser.toUserDetails(): DefaultUserDetails = DefaultUserDetails(this)
