import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin)
}

android {
  namespace = "com.zhangls.jsbridge.demo"
  compileSdk = libs.versions.compileSdk.get().toInt()
  buildToolsVersion = libs.versions.buildTool.get()

  defaultConfig {
    applicationId = "com.zhangls.jsbridge.demo"
    minSdk = libs.versions.minSdk.get().toInt()
    targetSdk = 36
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

  buildFeatures {
    viewBinding {
      enable = true
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }


  sourceSets {
    getByName("main") {
      jniLibs.setSrcDirs(listOf("libs"))
    }
  }
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_1_8
  }
}

dependencies {
  testImplementation(libs.junit)
  androidTestImplementation(libs.junit)
  androidTestImplementation(libs.androidx.extJunit)
  androidTestImplementation(libs.androidx.espresso)
  debugImplementation(libs.squareup.leakCanary)

  implementation(libs.androidx.core)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.constraintLayout)
  implementation(libs.google.material)
  implementation(libs.jetbrains.coroutines.android)

  implementation(libs.permissions)
  implementation(libs.utilcodex)

  implementation(project(":jsbridge"))
//  implementation(libs.zhangls.jsBridge)
}