package com.cnoke.base

/**
 * @date on 2022/1/8
 * @author huanghui
 * @title
 * @describe
 */
class ApiBase {

    companion object {
        val instance: ApiBase by lazy {
            ApiBase()
        }
    }
}