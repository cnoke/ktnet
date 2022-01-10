package com.cnoke.ktnet

import android.os.Bundle
import android.widget.TextView
import com.cnoke.base.activity.BaseActivity

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