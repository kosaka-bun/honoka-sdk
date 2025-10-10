version = globalLibs.versions.p.honoka.utils.get()

dependencies {
    api("cn.hutool:hutool-all:5.8.25")
    api("org.dom4j:dom4j:2.1.4")
    api("org.jsoup:jsoup:1.18.1")
    implementation("gui.ava:html2image:2.0.1") {
        exclude("xml-apis", "xml-apis")
    }
    api(libs.slf4j.api)
    implementation(libs.logback)
    compileOnly("org.jetbrains:annotations:24.0.0")
    runtimeOnly("org.bouncycastle:bcprov-jdk18on:1.78.1")
}

honoka.basic {
    publishing {
        default()
    }
}
