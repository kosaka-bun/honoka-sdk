@file:Suppress("DEPRECATION")

import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing

setupVersionAndPublishing("1.0.8")

dependencies {
    arrayOf(
        "org.apache.commons:commons-text:1.9",
        "org.apache.commons:commons-lang3:3.10",
        "commons-io:commons-io:2.11.0",
        "org.jsoup:jsoup:1.11.3"
    ).forEach {
        implementation(it)
        apiElements(it)
    }
    implementation("gui.ava:html2image:2.0.1")
}
