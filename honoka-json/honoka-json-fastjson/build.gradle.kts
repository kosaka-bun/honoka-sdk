import de.honoka.gradle.buildsrc.setupVersionAndPublishing

setupVersionAndPublishing("1.1.3")

dependencies {
    implementation("de.honoka.sdk:honoka-json-api:1.1.3".also {
        apiElements(it)
    })
    implementation("com.alibaba:fastjson:1.2.75")
}
