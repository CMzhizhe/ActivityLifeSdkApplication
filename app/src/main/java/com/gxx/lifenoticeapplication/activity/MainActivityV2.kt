package com.gxx.lifenoticeapplication.activity

import android.content.Intent
import android.os.Bundle
import com.gxx.lifenoticeapplication.activity.base.BaseActivity

class MainActivityV2: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun tvClick() {
        startNextActivity(Intent(this,MainActivityV3::class.java))
    }
}