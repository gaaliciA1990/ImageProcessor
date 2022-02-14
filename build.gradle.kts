val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.6.10"
}

group = "com.img_processor"
version = "0.0.1"
application {
    mainClass.set("com.img_processor.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true") // enables dev mode

}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation("com.sksamuel.scrimage:scrimage-core:4.0.24")
    implementation("com.sksamuel.scrimage:scrimage-filters:4.0.24")
}