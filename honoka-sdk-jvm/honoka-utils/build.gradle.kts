import de.honoka.gradle.util.dsl.common
import de.honoka.gradle.util.dsl.libs

honoka.basic.publishing.version = libs.common.versions.p.honoka.utils.get()

dependencies {
    api("cn.hutool:hutool-all:5.8.25")
    api("org.dom4j:dom4j:2.1.4")
    api("org.jsoup:jsoup:1.18.1")
    api(libs.slf4j.api)
    implementation(libs.logback)
    compileOnly("org.jetbrains:annotations:24.0.0")
    runtimeOnly("org.bouncycastle:bcpkix-jdk18on:1.80")
    compileOnly("com.formdev:flatlaf:3.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
}
