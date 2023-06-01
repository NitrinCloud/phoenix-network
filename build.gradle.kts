plugins {
    kotlin("jvm") version "1.8.21"

    `java-library`
    `maven-publish`
}

group = "net.nitrin.phoenix"
version = project.property("version")
    ?: "-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // https://mvnrepository.com/artifact/io.netty/netty-all
    api("io.netty:netty-all:${project.property("netty.version")}")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    api("com.google.code.gson:gson:${project.property("gson.version")}")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}