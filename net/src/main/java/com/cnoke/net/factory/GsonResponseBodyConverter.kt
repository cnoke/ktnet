package com.cnoke.net.factory

import okhttp3.ResponseBody
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import retrofit2.Converter

abstract class GsonResponseBodyConverter : Converter<ResponseBody, Any>{

    protected lateinit var gson: Gson
    protected lateinit var adapter: TypeAdapter<*>

    fun init(gson: Gson,adapter: TypeAdapter<*>): GsonResponseBodyConverter {
        this.gson = gson
        this.adapter = adapter
        return this
    }

}

