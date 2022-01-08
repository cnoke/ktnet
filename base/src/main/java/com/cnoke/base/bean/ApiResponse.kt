package com.cnoke.base.bean

/**
 * @date on 2022/1/7
 * @author huanghui
 * @title
 * @describe
 */
open class ApiResponse<T>(
    var data: T? = null,
    var errorCode: String = "",
    var errorMsg: String = ""
)