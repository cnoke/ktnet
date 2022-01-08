package com.cnoke.ktnet

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cnoke.net.await
import com.cnoke.net.factory.ApiResultCallAdapterFactory
import com.cnoke.net.factory.GsonConverterFactory
import com.cnoke.net.interception.HeadInterceptor
import com.cnoke.net.interception.LogInterceptor
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @date on 2022/1/7
 * @author huanghui
 * @title
 * @describe
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HeadInterceptor())
            .addInterceptor(LogInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://www.wanandroid.com/")
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(ApiResponse::class.java,MyGsonResponseBodyConverter()))
            .build()
        val service: TestServer = retrofit.create(TestServer::class.java)

        val view = findViewById<TextView>(R.id.tv_name)
        view.setOnClickListener {
            lifecycleScope.launch {
                val awaitBanner = service.awaitBanner().await {
                    Log.e("awaitBanner",it.toString())
                    listOf()
                }

                awaitBanner.let {
                    for(banner in it){
                        Log.e("awaitBanner",banner.title)
                    }
                }


                kotlin.runCatching {
                    val banner = service.banner()
                    for(item in banner){
                        Log.e("banner",item.title)
                    }
                }.onFailure {
                    Log.e("banner",it.toString())
                }

            }
        }
    }
}