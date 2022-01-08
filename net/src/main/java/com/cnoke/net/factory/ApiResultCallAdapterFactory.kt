package com.cnoke.net.factory

import com.cnoke.net.coroutines.Await
import com.cnoke.net.toResponse
import okhttp3.Request
import okio.Timeout
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @date on 2022/1/7
 * @author huanghui
 * @title
 * @describe
 */
class ApiResultCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        //检查returnType是否是Call<T>类型的
        if (getRawType(returnType) != Call::class.java) return null
        check(returnType is ParameterizedType) { "$returnType must be parameterized. Raw types are not supported" }
        //取出Call<T> 里的T，检查是否是Await<T>
        val apiResultType = getParameterUpperBound(0, returnType)
        // 如果不是 Await 则不由本 CallAdapter.Factory 处理
        if (getRawType(apiResultType) != Await::class.java) return null
        check(apiResultType is ParameterizedType) { "$apiResultType must be parameterized. Raw types are not supported" }

        //取出Await<T>中的T 也就是API返回数据对应的数据类型
//        val dataType = getParameterUpperBound(0, apiResultType)

        return ApiResultCallAdapter<Any>(apiResultType)
    }

}

class ApiResultCallAdapter<T>(private val type: Type) : CallAdapter<T, Call<Await<T>>> {
    override fun responseType(): Type = type

    override fun adapt(call: Call<T>): Call<Await<T>> {
        return ApiResultCall(call)
    }
}

class ApiResultCall<T>(private val delegate: Call<T>) : Call<Await<T>> {
    /**
     * 该方法会被Retrofit处理suspend方法的代码调用，并传进来一个callback,如果你回调了callback.onResponse，那么suspend方法就会成功返回
     * 如果你回调了callback.onFailure那么suspend方法就会抛异常
     *
     * 所以我们这里的实现是永远回调callback.onResponse,只不过在请求成功的时候返回的是ApiResult.Success对象，
     * 在失败的时候返回的是ApiResult.Failure对象，这样外面在调用suspend方法的时候就不会抛异常，一定会返回ApiResult.Success 或 ApiResult.Failure
     */
    override fun enqueue(callback: Callback<Await<T>>) {
        callback.onResponse(this@ApiResultCall, Response.success(delegate.toResponse()))
    }

    override fun clone(): Call<Await<T>> = ApiResultCall(delegate.clone())

    override fun execute(): Response<Await<T>> {
        throw UnsupportedOperationException("ApiResultCall does not support synchronous execution")
    }


    override fun isExecuted(): Boolean {
        return delegate.isExecuted
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean {
        return delegate.isCanceled
    }

    override fun request(): Request {
        return delegate.request()
    }

    override fun timeout(): Timeout {
        return delegate.timeout()
    }
}
