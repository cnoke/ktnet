package com.cnoke.net

import com.cnoke.net.coroutines.Await
import com.cnoke.net.coroutines.AwaitImpl
import retrofit2.Call

/**
 * @date on 2022/1/7
 * @author huanghui
 * @title
 * @describe
 */
public fun <T> Call<T>.toResponse() = toParser()

fun <T> Call<T>.toParser(): Await<T> = AwaitImpl(this)