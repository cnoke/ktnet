package com.cnoke.net.coroutines

import com.cnoke.net.utils.await
import retrofit2.Call

/**
 * @date on 2022/1/7
 * @author huanghui
 * @title
 * @describe
 */
internal class AwaitImpl<T>(
    private val call : Call<T>,
) : Await<T> {

    override suspend fun await(): T {

        return try {
            call.await()
        } catch (t: Throwable) {
            throw t
        }
    }
}