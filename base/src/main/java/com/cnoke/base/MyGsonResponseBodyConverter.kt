package com.cnoke.base

import com.cnoke.base.bean.ApiPagerResponse
import com.cnoke.base.bean.ApiResponse
import com.cnoke.net.exception.ParseException
import com.cnoke.net.factory.GsonResponseBodyConverter
import okhttp3.ResponseBody

/**
 * @date on 2022/1/8
 * @author huanghui
 * @title
 * @describe
 */
class MyGsonResponseBodyConverter : GsonResponseBodyConverter() {

    override fun convert(value: ResponseBody): Any {
        val jsonReader = gson.newJsonReader(value.charStream())
        val data = adapter.read(jsonReader) as ApiResponse<*>
        val t = data.data

        val listData = t as? ApiPagerResponse<*>
        if (listData != null) {
            //如果返回值值列表封装类，且是第一页并且空数据 那么给空异常 让界面显示空
            if (listData.isRefresh() && listData.isEmpty()) {
                throw ParseException(NetConstant.EMPTY_CODE, data.errorMsg)
            }
        }

        // errCode 不等于 SUCCESS_CODE，抛出异常
        if (data.errorCode != NetConstant.SUCCESS_CODE) {
            throw ParseException(data.errorCode, data.errorMsg)
        }

        return t!!
    }

}