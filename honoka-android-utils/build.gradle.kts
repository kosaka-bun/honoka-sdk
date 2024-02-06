import de.honoka.gradle.buildsrc.MavenPublish.setupAndroidAarVersionAndPublishing

setupAndroidAarVersionAndPublishing("1.0.2")

android {
    namespace = "de.honoka.sdk.util.android"
}

dependencies {
    implementation("de.honoka.sdk:honoka-framework-utils:1.0.4".also {
        api(it)
    })
    implementation("cn.hutool:hutool-all:5.8.18")
    implementation("org.nanohttpd:nanohttpd:2.3.1")
    implementation("com.j256.ormlite:ormlite-android:5.1")
}