package com.cnoke.net.interception

import com.cnoke.net.utils.NetKey
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 自定义头部参数拦截器，传入heads
 */
class HeadInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var builder = chain.request().newBuilder()
        if ("GET" == chain.request().method().toUpperCase()) {
            val getBuilder = chain.request().url().newBuilder()
            getBuilder.addQueryParameter(NetKey.KEY_VERSION,"1.1.40")
            getBuilder.addQueryParameter(NetKey.KEY_USER_ID,"")
            builder = chain.request().newBuilder().url(getBuilder.build()).build().newBuilder()
        }
        builder.addHeader(NetKey.KEY_SID, "10")
        builder.addHeader(NetKey.KEY_TOKEN, "")
        builder.addHeader(NetKey.KEY_VERSION, "1.1.40")
        builder.addHeader(NetKey.KEY_PLATFORM, "Android")
        builder.addHeader(NetKey.KEY_LANGUAGE, "zh-cn,zh;q=0.5")
        return chain.proceed(builder.build())
    }

}