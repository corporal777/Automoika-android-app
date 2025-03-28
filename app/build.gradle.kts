plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.google.services)
}

android {
    namespace = "kg.autojuuguch.automoikakg"
    compileSdk = 34

    defaultConfig {
        applicationId = "kg.autojuuguch.automoikakg"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables { useSupportLibrary = true }

        buildConfigField("String", "API_URL", "\"http://192.168.0.104:8080/\"")
        buildConfigField("String", "SOCKET_URL", "\"ws://192.168.0.104:8080/\"")
        //buildConfigField("String", "API_URL", "\"http://156.253.251.7:8080/\"")
        //buildConfigField("String", "SOCKET_URL", "\"ws://156.253.251.7:8080/\"")
    }

    signingConfigs {
        create("release"){
            keyAlias = "automoikaAlias"
            keyPassword = "automoikaAliasKey"
            storeFile = file("automoika.jks")
            storePassword = "automoikaKeyStore"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            if (signingConfigs.findByName("release") != null) signingConfig = signingConfigs["release"]
        }

        debug {
            if (signingConfigs.findByName("debug") != null) signingConfig = signingConfigs["debug"]
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // support
    implementation(libs.androidx.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.fragment.ktx)
    implementation(libs.activity.ktx)

    //google
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging)
    implementation(libs.google.auth)

    //navigation component
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    //paging3
    implementation(libs.paging.runtime.ktx)
    implementation(libs.paging.rxjava2.ktx)

    //rx-java2
    implementation(libs.rxjava2.rxandroid)
    implementation(libs.rxjava2.rxjava)
    implementation(libs.rxjava2.rxkotlin)

    //ui
    implementation(libs.core.splashscreen)
    implementation(libs.lisawray.groupie)
    implementation(libs.lisawray.groupie.viewbinding)
    implementation(libs.facebook.shimmer)
    implementation(libs.swipe.refresh.layout)
    implementation(libs.glide)
    implementation(libs.koil)
    implementation(libs.koil.transformation)
    implementation(libs.picasso)
    implementation(libs.simple.cropview)
    implementation(libs.lottie.animation)
    //implementation 'com.squareup.leakcanary:leakcanary-android:2.14'
    //implementation 'com.github.FrangSierra:RxFirebase:1.5.6'

    //utils
    implementation(libs.rxpermissions)
    implementation(libs.viewbinding.property.delegate)
    implementation(libs.viewbinding.property.delegate.reflection)

    // network
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.adapter.rxjava2)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.socket.io.client)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.websockets)
    implementation(libs.client.websocket)
    //implementation 'com.github.pwittchen:reactivenetwork-rx2:3.0.3'

    //koin
    implementation(libs.koin.android)
    implementation(libs.koin.android.compat)

    //Yandex Map
    implementation(libs.yandex.map)
    implementation(libs.google.map)
}