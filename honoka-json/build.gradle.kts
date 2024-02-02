subprojects {
    dependencies {
        implementation("de.honoka.sdk:honoka-utils:1.0.4")
    }

    tasks {
        compileJava {
            dependsOn(":honoka-utils:publish")
        }
    }
}
