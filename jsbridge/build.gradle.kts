import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.jetbrains.kotlin)
  id("maven-publish")
}

// 读取 git 的提交数量作为版本号
val gitVersionCode by lazy {
  val stdout = org.apache.commons.io.output.ByteArrayOutputStream()
  rootProject.exec {
    val cmd = "git rev-list HEAD --first-parent --count".split(" ")
    commandLine(cmd)
    standardOutput = stdout
  }
  stdout.toString(Charsets.UTF_8).trim().toInt()
}

// 读取 git 的 commit tag 作为应用的版本名，如果后面
val gitVersionTag by lazy {
  val stdout = org.apache.commons.io.output.ByteArrayOutputStream()
  rootProject.exec {
    val cmd = "git describe --tags".split(" ")
    commandLine(cmd)
    standardOutput = stdout
  }
  stdout.toString(Charsets.UTF_8).trim()
}

android {
  namespace = "com.zhangls.jsbridge"
  compileSdk = libs.versions.compileSdk.get().toInt()
  buildToolsVersion = libs.versions.buildTool.get()

  defaultConfig {
    minSdk = libs.versions.minSdk.get().toInt()

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles.add(file("consumer-rules.pro"))
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

  publishing {
    singleVariant("release") {
      withSourcesJar()
    }
  }
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_1_8
  }
}

afterEvaluate {
  android.libraryVariants.configureEach {
    val outputFile = outputs.first().outputFile
    tasks.named("assembleRelease").configure {
      doLast {
        copy {
          from(outputFile)
          into("../app/libs")
          rename(outputFile.name, "jsbridge.aar")
        }
      }
    }
  }

  publishing {
    publications {
      // Creates a Maven publication called "release".
      create<MavenPublication>("release") {
        // Applies the component for the release build variant.
        from(components["release"])

        groupId = "com.zhangls"
        artifactId = "jsbridge"
        version = gitVersionTag
      }
    }
  }
}

dependencies {
  testImplementation(libs.junit)
  androidTestImplementation(libs.junit)
  androidTestImplementation(libs.androidx.espresso)

  implementation(libs.androidx.annotations)
}