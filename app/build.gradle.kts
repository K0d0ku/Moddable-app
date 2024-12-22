plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.moddable"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.moddable"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)

    implementation ("androidx.webkit:webkit:1.7.0")
    implementation ("androidx.activity:activity-ktx:1.7.0")
    implementation ("androidx.appcompat:appcompat:1.3.1")
    implementation ("androidx.appcompat:appcompat:1.6.0")
    implementation ("androidx.activity:activity-compose:1.7.0")
    implementation ("androidx.compose.material:material-icons-extended:1.5.0")
    implementation ("org.vineflower:vineflower:1.10.1")
    implementation(libs.androidx.documentfile)
    implementation ("androidx.compose.ui:ui:1.5.0")
    implementation ("androidx.compose.material:material:1.5.0")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.5.0")
    implementation ("androidx.activity:activity-compose:1.7.2")
    compileOnly("org.jboss.windup.decompiler:decompiler-fernflower:6.0.0.Final")
    implementation ("androidx.compose.material3:material3:1.0.0")
    implementation ("androidx.compose.foundation:foundation:1.4.0")
    implementation ("androidx.activity:activity-compose:1.8.2")
    implementation ("androidx.compose.runtime:runtime:1.6.0")
    implementation ("androidx.navigation:navigation-compose:2.6.0")
//    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:1.9.0")
//    implementation("org.jetbrains.kotlin:kotlin-scripting-common:1.9.0")
//    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies:1.9.0")
//    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven:1.9.0")
//    implementation ("org.jetbrains.kotlin:kotlin-scripting-jvm:1.8.10")
//    implementation ("org.jetbrains.kotlin:kotlin-scripting-common:1.8.10")

    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}