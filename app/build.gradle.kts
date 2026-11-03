plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.badr.quranstudio"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.badr.quranstudio"
        minSdk = 26
        targetSdk = 36
        versionCode = 4
        versionName = "1.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        // Le keystore est fourni par la CI via des secrets (KEYSTORE_BASE64).
        // En local, les valeurs par défaut du keystore de debug prennent le relais.
        val keystoreFile = file("${rootDir}/debug.keystore")
        val keystorePassword = System.getenv("KEYSTORE_PASSWORD") ?: "android"
        val keyAliasName = System.getenv("KEY_ALIAS") ?: "androiddebugkey"
        val keyPasswordValue = System.getenv("KEY_PASSWORD") ?: "android"

        create("release") {
            storeFile = keystoreFile
            storePassword = keystorePassword
            keyAlias = keyAliasName
            keyPassword = keyPasswordValue
        }
        create("debugConfig") {
            storeFile = keystoreFile
            storePassword = keystorePassword
            keyAlias = keyAliasName
            keyPassword = keyPasswordValue
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            signingConfig = signingConfigs.getByName("debugConfig")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.okhttp)
}
