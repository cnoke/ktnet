package com.cnoke.base.bean

/**
 * @date on 2022/1/8
 * @author huanghui
 * @title
 * @describe
 */
sealed class ViewState{
    object Loading : ViewState()
    object Success : ViewState()
    object Empty : ViewState()
    data class Error(val throwable: Throwable) : ViewState()
}
