package com.cnoke.ktnet

import android.util.Log
import com.cnoke.base.MyGsonResponseBodyConverter
import com.cnoke.base.bean.ApiResponse
import com.cnoke.base.viewmodel.BaseViewModel
import com.cnoke.net.await
import com.cnoke.net.factory.ApiResultCallAdapterFactory
import com.cnoke.net.factory.GsonConverterFactory
import com.cnoke.net.interception.HeadInterceptor
import com.cnoke.net.interception.LogInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @date on 2022/1/8
 * @author huanghui
 * @title
 * @describe
 */
class MainViewModel : BaseViewModel() {

    fun banner(){
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HeadInterceptor())
            .addInterceptor(LogInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://www.wanandroid.com/")
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .addConverterFactory(
                GsonConverterFactory.create(
                ApiResponse::class.java,
                MyGsonResponseBodyConverter()
            ))
            .build()
        val service: TestServer = retrofit.create(TestServer::class.java)

        ktHttpRequest {
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