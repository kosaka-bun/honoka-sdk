import de.honoka.gradle.buildsrc.MavenPublish.setupAndroidAarVersionAndPublishing

setupAndroidAarVersionAndPublishing("1.0.0-dev")

android {
    namespace = "de.honoka.sdk.util.android"
}

dependencies {
    implementation("com.j256.ormlite:ormlite-android:5.1")
}