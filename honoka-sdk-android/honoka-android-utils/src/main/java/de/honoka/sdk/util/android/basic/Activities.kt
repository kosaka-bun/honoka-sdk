package de.honoka.sdk.util.android.basic

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.WindowManager
import de.honoka.sdk.util.android.activity.AbstractWebActivity
import de.honoka.sdk.util.android.activity.DefaultWebActivity
import de.honoka.sdk.util.android.activity.WebActivityExtras
import de.honoka.sdk.util.android.server.DefaultHttpServer
import de.honoka.sdk.util.kotlin.text.toJsonString
import de.honoka.sdk.util.kotlin.text.toJsonWrapper
import kotlin.reflect.KClass

private const val ACTIVITY_DEFAULT_EXTRAS_NAME = "defaultExtras"

fun Activity.startActivity(clazz: KClass<out Activity>, extras: Any? = null) {
    val intent = Intent(this, clazz.java)
    extras?.let {
        intent.putExtra(ACTIVITY_DEFAULT_EXTRAS_NAME, it.toJsonString())
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

fun <T : Any> Activity.getDefaultExtras(clazz: KClass<T>): T? = intent.run {
    getStringExtra(ACTIVITY_DEFAULT_EXTRAS_NAME)?.toJsonWrapper()?.toBean(clazz)
}

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
    window.decorView.systemUiVisibility = run {
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
    }
}
