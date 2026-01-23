version = commonLibs.versions.p.honoka.utils.get()

dependencies {
    api("cn.hutool:hutool-all:5.8.25")
    api("org.dom4j:dom4j:2.1.4")
    api("org.jsoup:jsoup:1.18.1")
    api(libs.slf4j.api)
    implementation(libs.logback)
    compileOnly("org.jetbrains:annotations:24.0.0")
    runtimeOnly("org.bouncycastle:bcpkix-jdk18on:1.80")
}

honoka.basic {
    publishing {
        default()
    }
}
