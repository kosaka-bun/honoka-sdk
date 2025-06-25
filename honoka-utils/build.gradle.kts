import de.honoka.gradle.plugin.basic.ext.MavenPublishDsl.publicationVersion
import de.honoka.gradle.util.dsl.implementationApi

dependencies {
    implementationApi("cn.hutool:hutool-all:5.8.25")
    implementationApi("org.dom4j:dom4j:2.1.4")
    implementationApi("org.jsoup:jsoup:1.18.1")
    implementation("gui.ava:html2image:2.0.1") {
        exclude("xml-apis", "xml-apis")
    }
    implementationApi(libs.slf4j.api)
    implementation(libs.logback)
    compileOnly("org.jetbrains:annotations:24.0.0")
    runtimeOnly("org.bouncycastle:bcprov-jdk18on:1.78.1")
}

publishing {
    publicationVersion = libs.versions.honoka.utils.get()
}
