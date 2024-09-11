
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.project"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.example.project"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load properties from local.properties
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        // Define a build config field with the API key
        manifestPlaceholders["API_KEY"] = properties.getProperty("API_KEY")

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.recyclerview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase
    implementation(libs.firebase.analytics)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)

    // Firebase Authentication
    implementation(libs.firebase.ui.auth)

    // Maps SDK for Android
    implementation(libs.play.services.maps)

    //user location
    implementation(libs.play.services.location)



    //cloud Storge
    implementation(libs.firebase.bom)
    implementation(libs.firebase.storage)

    //glide
    implementation (libs.glide)

    //chip
    implementation (libs.material)

    implementation (libs.play.services.maps.v1802)
    implementation (libs.play.services.location.v2101)

    //lottie
        implementation (libs.lottie)




}
