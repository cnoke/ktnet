package com.cnoke.net.coroutines

/**
 * @date on 2022/1/7
 * @author huanghui
 * @title
 * @describe
 */
interface Await<T> {

    suspend fun await(): T
}