package com.gxx.lifenoticeapplication.activity.mainprocess

import android.content.Intent
import android.os.Bundle
import com.gxx.activitylifelibrary.util.ProcessUtils
import com.gxx.lifenoticeapplication.activity.base.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tvProcessName.setText("我是"+ ProcessUtils.getProcessName(this)+"，我将启动登陆的页面，请点击我")
        tvProcessName.setOnClickListener{
            startNextActivity(Intent(this,LoginActivity::class.java))
        }
    }


}