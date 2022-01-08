package com.cnoke.base.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cnoke.base.NetConstant
import com.cnoke.base.bean.ViewState
import com.cnoke.base.error.code
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * @date on 2022/1/8
 * @author huanghui
 * @title
 * @describe
 */
open class BaseViewModel : ViewModel() {

    //只需要暴露一个LiveData，包括页面所有状态
    val viewStates : MutableLiveData<ViewState> = MutableLiveData()

    fun ktHttpRequest(requestDslClass: suspend CoroutineScope.() -> Unit){
        viewModelScope.launch {
            kotlin.runCatching {
                viewStates.value = ViewState.Loading
                requestDslClass.invoke(this)
                viewStates.value = ViewState.Success
            }.onFailure {
                if (isActive) {
                    if (it.code == NetConstant.EMPTY_CODE) {
                        viewStates.value = ViewState.Empty
                    }else{
                        viewStates.value = ViewState.Error(it)
                    }
                }
            }
        }
    }
}