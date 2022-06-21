package com.gxx.lifenoticeapplication.activity.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gxx.lifenoticeapplication.R
import com.gxx.activitylifelibrary.util.ProcessUtils

abstract class BaseActivity : AppCompatActivity(){
    lateinit var tvProcessName:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvProcessName = this.findViewById(R.id.tv_process_name);
    }

    /**
     * @date 创建时间: 2022/6/19
     * @auther gaoxiaoxiong
     * @description 启动下一个
     **/
    fun startNextActivity(intent:Intent){
        startActivity(intent)
    }
}