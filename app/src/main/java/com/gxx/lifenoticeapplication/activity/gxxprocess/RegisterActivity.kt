package com.gxx.lifenoticeapplication.activity.gxxprocess

import android.content.Intent
import android.os.Bundle
import com.gxx.lifenoticeapplication.activity.base.BaseActivity
import com.gxx.lifenoticeapplication.activity.videoprocess.MainActivityV3

class RegisterActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tvProcessName.setText("你好，我是:gxx进程的注册页面\n"+"你再次点击我，我也会创建一个:video进程")
        tvProcessName.setOnClickListener{
            startNextActivity(Intent(this,MainActivityV3::class.java))
        }
    }
}