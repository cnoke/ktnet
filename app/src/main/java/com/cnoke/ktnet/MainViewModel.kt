package com.cnoke.ktnet

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.cnoke.base.NetDelegates
import com.cnoke.base.viewmodel.BaseViewModel
import com.cnoke.net.async
import com.cnoke.net.tryAwait
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * @date on 2022/1/8
 * @author huanghui
 * @title
 * @describe
 */
class MainViewModel : BaseViewModel() {

    private val service : TestServer by NetDelegates()

    fun oldBanner(){
        viewModelScope.launch {
            //传统模式使用retrofit需要try catch

            val bannerAsync1 = async {
                var result : List<Banner>? = null
                kotlin.runCatching {
                   service.banner()
                }.onFailure {
                    Log.e("banner",it.toString())
                }.onSuccess {
                    result = it
                }
                result
            }

            val bannerAsync2 = async {
                var result : List<Banner>? = null
                kotlin.runCatching {
                    service.banner()
                }.onFailure {
                    Log.e("banner",it.toString())
                }.onSuccess {
                    result = it
                }
                result
            }

            bannerAsync1.await()
            bannerAsync2.await()
        }
    }

    fun banner(){

        ktHttpRequest {

            //单独处理异常 tryAwait会处理异常，如果异常返回空
            val awaitBanner = service.awaitBanner().tryAwait()
            awaitBanner?.let {
                for(banner in it){
                    Log.e("awaitBanner",banner.title)
                }
            }

            /**
             * 不处理异常 异常会直接抛出，给ktHttpRequest统一处理，
             * 回调到baseActivity中的initLoadingUiChange。进行网络异常界面展示
             */
            val awaitBannerError = service.awaitBanner().await()
        }
    }

    /**
     * 串行 await
     */
    fun serial(){
        ktHttpRequest {
            //先调用第一个接口await
            val awaitBanner1 = service.awaitBanner().await()
            //第一个接口完成后调用第二个接口
            val awaitBanner2 = service.awaitBanner().await()
        }
    }

    /**
     * 并行 async
     */
    fun parallel(){
        ktHttpRequest {
            val awaitBanner1 = service.awaitBanner().async(this)
            val awaitBanner2 = service.awaitBanner().async(this)

            //两个接口一起调用
            awaitBanner1.await()
            awaitBanner2.await()
        }
    }
}