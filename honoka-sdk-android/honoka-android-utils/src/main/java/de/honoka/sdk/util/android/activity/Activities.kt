package de.honoka.sdk.util.android.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.WindowManager
import de.honoka.sdk.util.android.web.server.DefaultHttpServer
import de.honoka.sdk.util.kotlin.text.toJsonString
import de.honoka.sdk.util.kotlin.text.toJsonWrapper
import kotlin.reflect.KClass

private const val ACTIVITY_BASIC_EXTRAS_NAME = "basicExtras"

fun Activity.startActivity(clazz: KClass<out Activity>, extras: Any? = null) {
    val intent = Intent(this, clazz.java)
    extras?.let {
        intent.putExtra(ACTIVITY_BASIC_EXTRAS_NAME, it.toJsonString())
    }
    startActivity(intent)
}

fun Activity.switchActivity(clazz: KClass<out Activity>, extras: Any? = null) {
    startActivity(clazz, extras)
    finish()
}

fun Activity.startRootWebActivty(
    webActivityClass: KClass<out AbstractWebActivity> = DefaultWebActivity::class,
    url: String = DefaultHttpServer.getUrlByPath("/")
) {
    switchActivity(webActivityClass, WebActivityExtras(url, true))
}

inline fun <reified T : Any> Activity.parseBasicExtras(): T = intent.run {
    getBasicExtrasString().toJsonWrapper().toBean(T::class)
}

@PublishedApi
internal fun Intent.getBasicExtrasString(): String = getStringExtra(ACTIVITY_BASIC_EXTRAS_NAME)!!

/**
 * 全屏化当前Activity
 */
@Suppress("DEPRECATION")
fun Activity.fullScreen() {
    //隐藏状态栏（手机时间、电量等信息显示的地方）
    window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )
    //隐藏虚拟按键
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
        View.SYSTEM_UI_FLAG_FULLSCREEN
}
