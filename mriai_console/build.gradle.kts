import java.util.regex.Pattern;
import java.util.regex.Pattern.compile

plugins {
    val kotlinVersion = "1.7.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.13.2"

}

group = "top.ncserver"
version = "0.2.0"
mirai.jvmTarget=org.gradle.api.JavaVersion.VERSION_11

repositories {
    maven("https://maven.aliyun.com/repository/public")

    mavenCentral()
}
dependencies {
    implementation("com.alibaba:fastjson:2.0.22")
    implementation("org.smartboot.socket:aio-pro:1.6.1")
}