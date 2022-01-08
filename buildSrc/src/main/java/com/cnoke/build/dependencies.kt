/**
 * @date on 2022/1/7
 * @author huanghui
 * @title
 * @describe
 */
object Versions {
    // Build tools and SDK
    val compileSdkVersion = 29
    val buildToolsVersion = "29.0.3"
    val minSdkVersion = 19
    val targetSdkVersion = 29
    val versionCode = 1
    val versionName = "1.1.40"

    // Android libraries
    val appcompat = "1.2.0"
    val coreKtx = "1.3.2"
    val constraintlayout = "2.0.4"
    val kotlin = "1.4.20"
    val coroutine = "1.3.9"
    val retrofit = "2.9.0"
    val lifecycle = "2.2.0"
}


object AndroidX {
    val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    val constraintlayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}"
    val material = "com.google.android.material:material:1.1.0"
}

object Kt {
    val stdlibJdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    val stdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    val test = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"
    val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val coroutineCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutine}"
    val coroutineAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutine}"
}

object Lifecycle{
    //lifecycle
    val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
    val common = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}"
    val extensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
    // viewModel
    val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    // liveData
    val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
    //利用liveData发送消息
    val unpeek = "com.kunminx.archi:unpeek-livedata:4.4.1-beta1"
}


object Http{
    val okhttp = "com.squareup.okhttp3:okhttp:4.9.1"
    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val retrofitGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
}

