/**
 * Module-level build configuration for the Gaming Thoughts app module.
 *
 * Configures Gradle plugins, Android SDK build settings, compile options,
 * Jetpack Compose features, and project dependencies.
 */

// Plugins required to build the application and enable Jetpack Compose support
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.gamingthoughts"

    // Target SDK configuration
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    // Default configuration options for all build variants
    defaultConfig {
        applicationId = "com.example.gamingthoughts"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val rawgApiKey = project.findProperty("RAWG_API_KEY") as? String ?: ""
        buildConfigField("String", "RAWG_API_KEY", "\"$rawgApiKey\"")
    }

    // Build types configuration (e.g., release, debug)
    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    // Java language compatibility configuration
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Enable Jetpack Compose & BuildConfig build features
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Jetpack Compose Bill of Materials (BOM) & Core UI components
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.icons.core)
    implementation(libs.androidx.compose.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // AndroidX Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Navigation & Security / Biometric Authentication
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.biometric.ktx)

    // Local Database (Room) & Annotation Processing (KSP)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Networking & API Integration (Retrofit)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // Image Loading (Coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)

    // Unit Testing
    testImplementation(libs.junit)

    // Android Instrumentation & UI Testing
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Compose Debug Tooling & Previews
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
