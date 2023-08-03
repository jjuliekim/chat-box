plugins {
    id("java")
}

group = "me.julie"
version = "1.0.0"

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
}

subprojects {
    apply(plugin = "java")

    group = rootProject.group
    version = rootProject.version

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            sourceCompatibility = "17"
            targetCompatibility = "17"
        }
    }

    repositories {
        mavenCentral()
        maven("https://jitpack.io/")
    }

    dependencies {
        implementation("com.google.code.gson:gson:2.10.1")
    }
}