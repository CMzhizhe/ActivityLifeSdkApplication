package com.gxx.lifenoticeapplication.activity.gxxprocess

import android.content.Intent
import android.os.Bundle
import com.gxx.lifenoticeapplication.activity.base.BaseActivity

class MainActivityV2: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tvProcessName.setText("你好，我是:gxx进程\n"+"你再次点击我，我也会创建一个:gxx的注册页面")
        tvProcessName.setOnClickListener{
            startNextActivity(Intent(this,RegisterActivity::class.java))
        }
    }


}