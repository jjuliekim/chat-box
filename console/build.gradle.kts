plugins {
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation("com.neovisionaries:nv-websocket-client:2.14")
}

application {
    mainClass = "me.julie.chatlink.console.Main"
}

tasks {
    getByName<JavaExec>("run") {
        standardInput = System.`in`
    }
}