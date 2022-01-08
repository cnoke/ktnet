package com.cnoke.base.viewmodel

import androidx.lifecycle.*

/**
 * @date on 2022/1/8
 * @author huanghui
 * @title
 * @describe
 */

fun <T> LiveData<T>.observeState(
    lifecycleOwner: LifecycleOwner,
    action: (T) -> Unit
) {
    this.distinctUntilChanged().observe(lifecycleOwner){
        action.invoke(it)
    }
}