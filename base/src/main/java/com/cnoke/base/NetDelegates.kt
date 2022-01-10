package com.cnoke.base

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * @date on 2022/1/10
 * @author huanghui
 * @title
 * @describe
 */
class NetDelegates<T : Any> (private val url : String? = null): ReadOnlyProperty<Any?,T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val cf = property.returnType.classifier as KClass<T>
        return NetManager.getService(cf.java,url)
    }

}