plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.zhangls.jsbridge.demo"
    compileSdk = libs.versions.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.buildTool.get()

    defaultConfig {
        applicationId = "com.zhangls.jsbridge.demo"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    sourceSets {
        getByName("main") {
            jniLibs.setSrcDirs(listOf("libs"))
        }
    }
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.extJunit)
    androidTestImplementation(libs.espresso)

    implementation(libs.coreKtx)
    implementation(libs.appcompat)
    implementation(libs.materialDesign)
    debugImplementation(libs.leakCanary)
    implementation(libs.coroutinesAndroid)
    implementation(libs.activity)
    implementation(libs.constraintLayout)

    implementation(libs.xxpermissions)
    implementation(libs.tencentX5)
    implementation(project(":jsbridge"))
//    implementation(libs.jsBridge)
}