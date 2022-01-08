package com.cnoke.net.exception

import java.io.IOException

/**
 * @date on 2022/1/8
 * @author huanghui
 * @title
 * @describe
 */
class ParseException(code: String, message: String?) :
    IOException(message) {

    val errorCode: String = code

    override fun getLocalizedMessage(): String {
        return errorCode
    }

    override fun toString(): String {
        return """${javaClass.name}:
            Code=$errorCode message=$message"""
    }
}
