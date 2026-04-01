plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.sourav.aiotclub1"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sourav.aiotclub1"
        minSdk = 24
        targetSdk = 35
        versionCode = 18
        versionName = "1.1.15"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            ndk {
                debugSymbolLevel = "FULL"
            }
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures{
        dataBinding =true
        viewBinding=true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    ndkVersion = "29.0.13599879 rc2"
}

dependencies {

    implementation(libs.appcompat)
    implementation (libs.material)
    implementation (libs.picasso)
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-core:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-hls:2.19.1")

    implementation(libs.material)
    implementation (libs.play.services.auth)
    implementation(platform(libs.firebase.bom))
    implementation (libs.android.spinkit)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)


    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation(libs.constraintlayout.v204)
    implementation (libs.glide)
    implementation(libs.firebase.storage)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.impress)
    implementation(libs.androidx.drawerlayout)
    implementation(libs.firebase.inappmessaging.display)
    implementation(libs.androidx.preference)
    annotationProcessor (libs.glide.compiler)
    implementation(libs.firebase.auth)


    implementation (libs.firebase.firestore)
    implementation (libs.lottie)
    implementation(libs.appcompat)
    implementation(libs.viewpager2)
    implementation(libs.roundedimageview)
    implementation (libs.firebase.database)
    implementation (libs.glide.v4160)
    implementation (libs.app.update)
    implementation (libs.material)
    implementation ("com.google.code.gson:gson:2.13.2")
    implementation(libs.shimmer)
    testImplementation(libs.junit)
    implementation (libs.firebase.messaging)
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor (libs.glide.compiler)
    implementation ("androidx.core:core:1.17.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation (libs.kotlin.stdlib.jdk7)
    implementation ("com.github.Foysalofficial:NafisBottomNav:5.0")
    implementation (libs.firebase.appcheck.debug)
    implementation(libs.ucrop)
    implementation(libs.circleimageview)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}