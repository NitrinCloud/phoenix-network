plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "net.nitrin.phoenix"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // https://mvnrepository.com/artifact/io.netty/netty-all
    implementation("io.netty:netty-all:4.1.93.Final")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}