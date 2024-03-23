import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing

setupVersionAndPublishing("1.0.11-dev")

dependencies {
    arrayOf(
        "org.apache.commons:commons-text:1.9",
        "org.apache.commons:commons-lang3:3.10",
        "commons-io:commons-io:2.11.0",
        "org.dom4j:dom4j:2.1.3",
        "org.jsoup:jsoup:1.11.3"
    ).forEach {
        implementation(it)
        api(it)
    }
    implementation("gui.ava:html2image:2.0.1") {
        exclude("xml-apis", "xml-apis")
    }
    compileOnly("org.jetbrains:annotations:24.0.0")
}
