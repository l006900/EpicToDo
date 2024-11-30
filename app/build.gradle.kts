plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.epictodo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.epictodo"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("androidx.appcompat:appcompat:1.7.0")

    // Room components
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    androidTestImplementation("androidx.room:room-testing:2.6.1")

    //WorkManager
    implementation("androidx.work:work-runtime:2.10.0")
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("androidx.work:work-rxjava2:2.10.0")
    implementation("androidx.work:work-gcm:2.10.0")
    implementation("androidx.work:work-testing:2.10.0")

    //material
    implementation("com.google.android.material:material:1.12.0")


//    implementation("androidx.appcompat:appcompat-resources:1.7.0")
//    implementation("androidx.appcompat:appcompat:1.7.0")
//    implementation("com.android.support:design:28.0.0")

}