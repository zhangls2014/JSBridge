plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
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
    stdout.toString().trim().toInt()
}

// 读取 git 的 commit tag 作为应用的版本名，如果后面
val gitVersionTag by lazy {
    val stdout = org.apache.commons.io.output.ByteArrayOutputStream()
    rootProject.exec {
        val cmd = "git describe --tags".split(" ")
        commandLine(cmd)
        standardOutput = stdout
    }
    stdout.toString().trim()
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

    kotlinOptions {
        jvmTarget = "1.8"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
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
    androidTestImplementation(libs.extJunit)
    androidTestImplementation(libs.espresso)

    implementation(libs.coreKtx)
    implementation(libs.tencentX5)
}