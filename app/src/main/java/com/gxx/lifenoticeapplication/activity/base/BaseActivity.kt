package com.gxx.lifenoticeapplication.activity.base

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gxx.lifenoticeapplication.R
import com.gxx.activitylifelibrary.util.ProcessUtils
import com.gxx.lifenoticeapplication.activity.dialog.DialogActivity
import com.gxx.lifenoticeapplication.activity.mainprocess.MainActivity

abstract class BaseActivity : AppCompatActivity(){
    private val TAG = "LoginActivity"
    lateinit var tvProcessName:TextView
    lateinit var tvDialog:TextView
    lateinit var tvPopuWindow:TextView
    lateinit var viewDialogLine:View
    lateinit var viewPopupWindowLine:View
    lateinit var viewActivityDialogLine:View
    lateinit var tvActivityDialog:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvProcessName = this.findViewById(R.id.tv_process_name);
        tvDialog = this.findViewById(R.id.tv_dialog)
        tvPopuWindow = this.findViewById(R.id.tv_popuwindow)
        viewDialogLine = this.findViewById(R.id.view_dialog_line)
        viewPopupWindowLine = this.findViewById(R.id.view_popuwindow_line)
        tvActivityDialog = this.findViewById(R.id.tv_dialog_acitivty)
        viewActivityDialogLine= this.findViewById(R.id.view_dialog_acitivty_line)
    }

    /**
     * @date 创建时间: 2022/6/19
     * @auther gaoxiaoxiong
     * @description 启动下一个
     **/
    fun startNextActivity(intent:Intent){
        startActivity(intent)
    }

    /**
     * @date 创建时间:2022/8/15 0015
     * @author gaoxiaoxiong
     * @descriptiion 创建一个dialog
     **/
    fun createDialog(){
        tvDialog.setOnClickListener{
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("问题：")
            builder.setMessage("我是 LoginActivity 的页面?")
            builder.setIcon(android.R.mipmap.sym_def_app_icon)
            //点击对话框以外的区域是否让对话框消失
            builder.setCancelable(true)
            //设置正面按钮
            builder.setPositiveButton("是的", DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(LoginActivity@this, "是的", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            })

            val dialog: AlertDialog = builder.create()
            //对话框显示的监听事件
            dialog.setOnShowListener(DialogInterface.OnShowListener { Log.e(TAG, "对话框显示了") })
            dialog.show()
        }
    }

    /**
     * @date 创建时间:2022/8/15 0015
     * @author gaoxiaoxiong
     * @descriptiion 创建popuwindow
     **/
    fun createPopuwindow(){
        val rootview = LayoutInflater.from(this).inflate(R.layout.layout_popuwindow, null)
        val tvCancle = rootview.findViewById<TextView>(R.id.tv_cancle)
        val popuwindow = PopupWindow(
            rootview,
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true
        )
        popuwindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
        tvCancle.setOnClickListener{
            popuwindow.dismiss()
        }
    }

    /**
     * @date 创建时间:2022/8/15 0015
     * @author gaoxiaoxiong
     * @descriptiion 启动dialogactivity
     **/
    fun createDialogActivity(intent:Intent){
        startActivity(intent)
    }
}