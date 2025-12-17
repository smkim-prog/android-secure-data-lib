plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.ksign.crypto"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // 최소한의 의존성만 유지
    implementation(libs.androidx.core.ktx)

    // Room (optional - 사용자가 DB 암호화 사용할 때만 필요)
    compileOnly("androidx.room:room-runtime:2.6.1")

    // 테스트
    testImplementation(libs.junit)
}

// JitPack publication
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.ksign"
                artifactId = "secure-data"
                version = "1.0.0"
            }
        }
    }
}