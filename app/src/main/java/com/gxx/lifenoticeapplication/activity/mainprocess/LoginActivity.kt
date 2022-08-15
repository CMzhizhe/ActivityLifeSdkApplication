package com.gxx.lifenoticeapplication.activity.mainprocess

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.gxx.lifenoticeapplication.activity.base.BaseActivity
import com.gxx.lifenoticeapplication.activity.dialog.DialogActivity
import com.gxx.lifenoticeapplication.activity.gxxprocess.MainActivityV2


class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tvProcessName.setText("你好，我是登陆页面\n"+"你再次点击我，我会启动:gxx多进程")
        tvProcessName.setOnClickListener{
            startNextActivity(Intent(this, MainActivityV2::class.java))
        }

        viewDialogLine.visibility = View.VISIBLE
        tvDialog.visibility = View.VISIBLE
        tvDialog.setOnClickListener{
            createDialog()
        }
        viewPopupWindowLine.visibility = View.VISIBLE
        tvPopuWindow.visibility = View.VISIBLE
        tvPopuWindow.setOnClickListener{
            createPopuwindow()
        }

        viewDialogLine.visibility = View.VISIBLE
        tvActivityDialog.visibility = View.VISIBLE
        tvActivityDialog.setOnClickListener{
            createDialogActivity(Intent(this,DialogActivity::class.java))
        }
    }


}