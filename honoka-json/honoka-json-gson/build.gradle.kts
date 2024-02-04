import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing

setupVersionAndPublishing("1.1.3")

dependencies {
    implementation("de.honoka.sdk:honoka-json-api:1.1.3".also {
        api(it)
    })
    implementation("com.google.code.gson:gson:2.8.6")
}