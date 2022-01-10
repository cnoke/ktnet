package com.cnoke.net.factory

import com.cnoke.net.coroutines.Await
import com.cnoke.net.entity.ParameterizedTypeImpl
import com.cnoke.net.utils.Utils
import retrofit2.Retrofit
import okhttp3.ResponseBody
import okhttp3.RequestBody
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import retrofit2.Converter
import java.lang.NullPointerException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class GsonConverterFactory private constructor(private var responseCz : Class<*>
,var responseConverter : GsonResponseBodyConverter
,var requestBodyConverter: GsonRequestBodyConverter
, private val gson: Gson) : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        var adapter : TypeAdapter<*>? = null
        //检查是否是Await<T>
        if (Utils.getRawType(type) == Await::class.java && type is ParameterizedType){
            //取出Await<T>中的T
            val awaitType =  Utils.getParameterUpperBound(0, type)
            if(awaitType != null){
                adapter = gson.getAdapter(TypeToken.get(ParameterizedTypeImpl[responseCz,awaitType]))
            }
        }
        if(adapter == null){
            adapter= gson.getAdapter(TypeToken.get(ParameterizedTypeImpl[responseCz,type]))
        }
        return responseConverter.init(gson, adapter!!)
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return requestBodyConverter.init(gson, adapter as TypeAdapter<Any>)
    }

    companion object {
        /**
         * Create an instance using `gson` for conversion. Encoding to JSON and
         * decoding from JSON (when no charset is specified by a header) will use UTF-8.
         */
        /**
         * Create an instance using a default [Gson] instance for conversion. Encoding to JSON and
         * decoding from JSON (when no charset is specified by a header) will use UTF-8.
         */
        @JvmOverloads  // Guarding public API nullability.
        fun create(responseCz : Class<*>, responseConverter : GsonResponseBodyConverter
                   , requestBodyConverter: GsonRequestBodyConverter? =null
                   , gson: Gson? = Gson()): GsonConverterFactory {
            if (gson == null) throw NullPointerException("gson == null")
            var myRequestBodyConverter = requestBodyConverter
            if (myRequestBodyConverter == null) {
                myRequestBodyConverter =  GsonRequestBodyConverter()
            }
            return GsonConverterFactory(responseCz,responseConverter,myRequestBodyConverter,gson)
        }
    }
}