import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing
import de.honoka.gradle.buildsrc.implementationApi

setupVersionAndPublishing(libs.versions.honoka.utils.get())

dependencies {
    implementationApi("cn.hutool:hutool-all:5.8.25")
    implementationApi("org.dom4j:dom4j:2.1.3")
    implementationApi("org.jsoup:jsoup:1.18.1")
    implementation("gui.ava:html2image:2.0.1") {
        exclude("xml-apis", "xml-apis")
    }
    implementationApi(libs.slf4j.api)
    implementation(libs.logback)
    compileOnly("org.jetbrains:annotations:24.0.0")
    runtimeOnly("org.bouncycastle:bcprov-jdk18on:1.78.1")
}
