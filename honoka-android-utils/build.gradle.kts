import de.honoka.gradle.buildsrc.MavenPublish.setupAndroidAarVersionAndPublishing

setupAndroidAarVersionAndPublishing("1.0.3-dev")

android {
    namespace = "de.honoka.sdk.util.android"
}

dependencies {
    arrayOf(
        "cn.hutool:hutool-all:5.8.18",
        "com.j256.ormlite:ormlite-android:5.1"
    ).forEach {
        implementation(it)
        api(it)
    }
    implementation("de.honoka.sdk:honoka-framework-utils:1.0.4")
    implementation("org.nanohttpd:nanohttpd:2.3.1")
}