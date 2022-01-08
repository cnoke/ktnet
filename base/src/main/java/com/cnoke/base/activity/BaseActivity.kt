package com.cnoke.base.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.cnoke.base.bean.ViewState
import com.cnoke.base.viewmodel.observeState
import com.cnoke.base.viewmodel.BaseViewModel
import java.lang.reflect.ParameterizedType

/**
 * @date on 2022/1/8
 * @author huanghui
 * @title
 * @describe
 */
open class BaseActivity<VM : BaseViewModel> : AppCompatActivity(){

    //当前Activity绑定的 ViewModel
    lateinit var mViewModel : VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //生成ViewModel
        mViewModel = createViewModel()
        initLoadingUiChange(mViewModel)
    }

    private fun initLoadingUiChange(mViewModel: VM) {
        mViewModel.viewStates.observeState(this){
            when(it){
                is ViewState.Loading -> Log.e("huanghui","Loading")
                is ViewState.Success -> Log.e("huanghui","Success")
                is ViewState.Empty -> Log.e("huanghui","Empty")
                is ViewState.Error -> Log.e("huanghui","Error ${it.throwable}")
            }
        }
    }

    private fun createViewModel(): VM {
        return ViewModelProvider(this).get(getVmClazz(this))
    }

    @Suppress("UNCHECKED_CAST")
    fun <VM> getVmClazz(obj: Any): VM {
        return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as VM
    }

}