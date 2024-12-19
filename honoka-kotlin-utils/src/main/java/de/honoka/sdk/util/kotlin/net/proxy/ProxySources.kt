package de.honoka.sdk.util.kotlin.net.proxy

import cn.hutool.http.HttpUtil
import de.honoka.sdk.util.kotlin.code.repeatCatching
import de.honoka.sdk.util.kotlin.code.tryBlock
import de.honoka.sdk.util.kotlin.net.http.browserApiHeaders
import de.honoka.sdk.util.kotlin.net.http.browserHeaders
import de.honoka.sdk.util.kotlin.net.http.randomProxy
import de.honoka.sdk.util.kotlin.text.forEachWrapper
import de.honoka.sdk.util.kotlin.text.toJsonWrapper
import org.jsoup.Jsoup
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.hasAnnotation

@Suppress("unused")
object ProxySources {
    
    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FUNCTION)
    annotation class ProxySource(val name: String, val order: Double = Double.MAX_VALUE)
    
    @Suppress("UNCHECKED_CAST")
    val all: List<() -> List<String>> = run {
        val funList = this::class.declaredFunctions.filter { it.hasAnnotation<ProxySource>() }
        funList.sortedBy { it.findAnnotations(ProxySource::class).first().order }.map {
            {
                val list = it.call(this) as List<String>
                val regex = Regex("\\d{1,3}(\\.\\d{1,3}){3}:\\d{1,5}")
                list.map { it.trim() }.filter { regex.matches(it) }
            }
        }
    }
    
    private fun httpGet(url: String, useApiHeaders: Boolean = false): String {
        val request = HttpUtil.createGet(url).apply {
            if(useApiHeaders) {
                browserApiHeaders()
            } else {
                browserHeaders()
            }
            randomProxy(false)
        }
        return request.execute().body()
    }
    
    private inline fun parseFromTable(
        urlTemplate: (Int) -> String,
        cssSelector: String,
        ipIndex: Int = 0,
        portIndex: Int = 1
    ): List<String> {
        val list = ArrayList<String>()
        repeatCatching(5) { i ->
            val doc = tryBlock(3) {
                val res = httpGet(urlTemplate(i + 1))
                Jsoup.parse(res)
            }
            val table = doc.expectFirst(cssSelector)
            table.getElementsByTag("tr").forEach {
                val tdList = it.getElementsByTag("td")
                list.add("${tdList[ipIndex].text().trim()}:${tdList[portIndex].text().trim()}")
            }
        }
        return list
    }
    
    @ProxySource("docip", 1.0)
    fun docip(): List<String> {
        val url = "https://www.docip.net/data/free.json"
        val list = ArrayList<String>()
        val res = tryBlock(3) { httpGet(url, true) }
        res.toJsonWrapper().getArray("data").forEachWrapper {
            list.add(it.getStr("ip").trim())
        }
        return list
    }
    
    @ProxySource("zdaye", 2.0)
    fun zdaye(): List<String> {
        fun url(page: Int) = "https://www.zdaye.com/free/$page/?checktime=5&sleep=2"
        return parseFromTable(::url, "#ipc tbody")
    }
    
    @ProxySource("ip3366", 3.0)
    fun ip3366(): List<String> {
        fun url(page: Int) = "http://www.ip3366.net/free/?stype=1&page=$page"
        return parseFromTable(::url, "#list tbody")
    }
    
    @ProxySource("89ip", 4.0)
    fun the89ip(): List<String> {
        fun url(page: Int) = "https://www.89ip.cn/index_$page.html"
        return parseFromTable(::url, "div.fly-panel tbody")
    }
    
    @ProxySource("kuaidaili", 5.0)
    fun kuaidaili(): List<String> {
        fun url(page: Int) = "https://www.kuaidaili.com/free/inha/$page/"
        return parseFromTable(::url, "#table__free-proxy tbody")
    }
}
