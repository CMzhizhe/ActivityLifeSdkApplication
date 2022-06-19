package com.gxx.lifenoticeapplication.activity

import android.os.Bundle
import com.gxx.lifenoticeapplication.activity.base.BaseActivity

class MainActivityV3: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun tvClick() {
       tvProcessName.setText("我是最后一个多进程啦")
    }
}