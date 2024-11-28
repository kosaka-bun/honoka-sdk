import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing
import de.honoka.gradle.buildsrc.implementationApi

setupVersionAndPublishing(libs.versions.honoka.utils.get())

dependencies {
    implementationApi("org.apache.commons:commons-text:1.9")
    implementationApi("org.apache.commons:commons-lang3:3.10")
    implementationApi("commons-io:commons-io:2.11.0")
    implementationApi("org.dom4j:dom4j:2.1.3")
    implementationApi("org.jsoup:jsoup:1.11.3")
    implementation("gui.ava:html2image:2.0.1") {
        exclude("xml-apis", "xml-apis")
    }
    compileOnly("org.jetbrains:annotations:24.0.0")
    runtimeOnly("org.bouncycastle:bcprov-jdk18on:1.78.1")
}
