package com.gxx.lifenoticeapplication.activity.mainprocess

import android.content.Intent
import android.os.Bundle
import com.gxx.activitylifelibrary.util.ProcessUtils
import com.gxx.lifenoticeapplication.activity.base.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tvProcessName.text = "我的进程名称："+ ProcessUtils.getProcessName(this)+"，点击我将进入下一个页面"
        tvProcessName.setOnClickListener{
            startNextActivity(Intent(this,LoginActivity::class.java))
        }
    }


}