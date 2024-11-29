plugins {
    alias(libs.plugins.android.application) // Correct alias for Android Application plugin
    alias(libs.plugins.google.gms.google.services) // Correct alias for Google Services plugin
}

android {
    namespace = "com.example.wheretogo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.wheretogo"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8 // Set Java version compatibility
        targetCompatibility = JavaVersion.VERSION_1_8 // Ensure compatibility with Java 8
    }
}

dependencies {
    // Firebase BOM ensures consistent versions for Firebase libraries
    implementation(platform("com.google.firebase:firebase-bom:32.1.1"))

    // Individual Firebase libraries (no need for specific versions)
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.squareup.picasso:picasso:2.71828")




    // Other libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
