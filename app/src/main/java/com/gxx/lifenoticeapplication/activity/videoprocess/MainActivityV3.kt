package com.gxx.lifenoticeapplication.activity.videoprocess

import android.os.Bundle
import com.gxx.lifenoticeapplication.activity.base.BaseActivity

class MainActivityV3: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tvProcessName.setText("你好，我是video进程\n")
    }


}