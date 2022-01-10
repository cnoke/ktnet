package com.cnoke.ktnet

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cnoke.base.bean.ApiResponse
import com.cnoke.base.MyGsonResponseBodyConverter
import com.cnoke.base.activity.BaseActivity
import com.cnoke.net.await
import com.cnoke.net.factory.ApiResultCallAdapterFactory
import com.cnoke.net.factory.GsonConverterFactory
import com.cnoke.net.interception.HeadInterceptor
import com.cnoke.net.interception.LogInterceptor
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @date on 2022/1/7
 * @author huanghui
 * @title
 * @describe
 */
class MainActivity : BaseActivity<MainViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = findViewById<TextView>(R.id.tv_name)
        view.setOnClickListener {
            mViewModel.banner()
        }

    }
}