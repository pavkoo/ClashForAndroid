plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = buildTargetSdkVersion

    defaultConfig {
        minSdk = buildMinSdkVersion
        targetSdk = buildTargetSdkVersion

        versionCode = buildVersionCode
        versionName = buildVersionName

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation(kotlin("stdlib-jdk7"))
    implementation("androidx.core:core-ktx:$coreVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")

    api("androidx.drawerlayout:drawerlayout:1.1.1")
    api("androidx.recyclerview:recyclerview:$recyclerviewVersion")

    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("com.squareup.retrofit2:converter-gson:2.9.0")
    api("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    api("com.google.code.gson:gson:2.8.6")
    api("com.squareup.okhttp3:okhttp:3.14.9")
    api("com.squareup.okhttp3:logging-interceptor:3.14.9")
    api("io.reactivex.rxjava2:rxjava:2.2.10")
    api("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")
}

repositories {
    mavenCentral()
    google()
}
