plugins {
    kotlin("jvm") version "1.5.0"
    `java-gradle-plugin`
}

repositories {
    maven {
        setUrl("https://maven.aliyun.com/repository/public/")
    }
    mavenCentral()
    google()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("serialization"))
    implementation("com.android.tools.build:gradle:4.2.1")
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.5.0-1.0.0-alpha10")
}

gradlePlugin {
    plugins {
        create("golang") {
            id = "clash-build"
            implementationClass = "com.github.kr328.clash.tools.ClashBuildPlugin"
        }
    }
}
