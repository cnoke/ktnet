package com.cnoke.ktnet

import com.cnoke.net.coroutines.Await
import retrofit2.http.GET

/**
 * @date on 2022/1/8
 * @author huanghui
 * @title
 * @describe
 */
interface TestServer {
    @GET("banner/json")
    suspend fun awaitBanner(): Await<List<Banner>>

    @GET("banner/json")
    suspend fun banner(): List<Banner>

}