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

//    @Throws(IOException::class)
//    override fun convert(value: ResponseBody): Any {
//        val jsonReader = gson.newJsonReader(value.charStream())
//        val data = adapter.read(jsonReader) as ApiResponse<*>
//        val t = data.data
//
//        val listData = t as? ApiPagerResponse<*>
//        if (listData != null) {
//            //如果返回值值列表封装类，且是第一页并且空数据 那么给空异常 让界面显示空
//            if (listData.isRefresh() && listData.isEmpty()) {
//                throw ParseException(NetConstant.EMPTY_CODE, data.errorMsg)
//            }
//        }
//
//        // errCode 不等于 SUCCESS_CODE，抛出异常
//        if (data.errorCode != NetConstant.SUCCESS_CODE) {
//            throw ParseException(data.errorCode, data.errorMsg)
//        }
//
//        return t!!
//    }

}

