package com.cnoke.base

import com.cnoke.base.bean.ApiResponse
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
object NetManager {

    private var debug = false
    private var okHttpClient : OkHttpClient? = null
    private val serviceMap = hashMapOf<String, Any>()
    private const val baseURL = "https://www.wanandroid.com/"

    fun setDebug(debug: Boolean) {
        this.debug = debug
    }

    fun <S> getService(serviceClass: Class<S>,url : String? = null): S {
        var myUrl = url
        if(myUrl == null){
            myUrl =  baseURL
        }
        val className = serviceClass.name
        val service = serviceMap[className]
        return if (service == null) {
            val newService = Retrofit
                .Builder()
                .baseUrl(myUrl)
                .addCallAdapterFactory(ApiResultCallAdapterFactory())
                .addConverterFactory(
                    GsonConverterFactory.create(
                        ApiResponse::class.java,
                        MyGsonResponseBodyConverter()
                    ))
                .build()
                .create(serviceClass)
            serviceMap[className] = newService!!
            newService
        } else {
            service as S
        }
    }


    fun getClient(): OkHttpClient {
        if(okHttpClient == null){
            var builder = OkHttpClient
                .Builder()
            if (debug) {
                builder = builder.addInterceptor(LogInterceptor())
            }
            builder = builder.addInterceptor(HeadInterceptor())
            okHttpClient  = builder.build()
        }

        return okHttpClient!!
    }


}