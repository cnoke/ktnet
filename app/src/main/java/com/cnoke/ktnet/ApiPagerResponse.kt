package com.cnoke.ktnet

import java.io.Serializable

/**
 * @date on 2022/1/7
 * @author huanghui
 * @title
 * @describe
 */
/**
 * 服务器返回的列表数据基类
 */
data class ApiPagerResponse<T>(
    var records: ArrayList<T> = ArrayList(),
    var pages: Int = 0,
    var current: Int = 1,
    var size: Int = 0,
    var total: Int = 0
) : Serializable {
    /**
     * 数据是否为空
     */
    fun isEmpty() = total == 0

    /**
     * 是否为刷新
     */
    fun isRefresh() = current == 1

    /**
     * 是否还有更多数据
     */
    fun hasMore() : Boolean = current < pages
}