import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing

setupVersionAndPublishing(libs.versions.honoka.json.fastjson.get())

dependencies {
    implementation("de.honoka.sdk:honoka-json-api:1.1.3".also {
        api(it)
    })
    implementation("com.alibaba:fastjson:1.2.75")
}
