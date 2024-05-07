package com.gxx.activitylifelibrary.service

import android.app.Service
import android.content.Intent
import android.os.*
import com.gxx.activitylifelibrary.model.LifeModel
import com.gxx.activitylifelibrary.util.LogUtil

class ActivityLifeService : Service() {
    companion object {
        const val WHAT_MAIN_NOINIT = -1//不需要初始化
        const val WHAT_MAIN_INIT = 0 //主的初始化
        const val WHAT_STATE_LIFE = 1//处理生命周期的
        const val WHAT_LIFT_CHECK_AGAIN = 2//再次去检查生命周期
        const val BUNDLE_MODEL = "model"
    }

    private var mMainMessenger: Messenger? = null;
    private val mMessenger = Messenger(object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.data == null) {
                LogUtil.d("msg.data为null")
                return
            }
            val model = msg.data.getSerializable(BUNDLE_MODEL) as LifeModel
            if (msg.what == WHAT_MAIN_INIT && msg.replyTo != null) {
                mMainMessenger = msg.replyTo;
                sendMessage(model,msg)
            } else if (mMainMessenger != null && msg.what == WHAT_STATE_LIFE) {
                sendMessage(model,msg)
            }
        }
    })

    private fun sendMessage(lifeModel: LifeModel,msg: Message){
        val message = Message.obtain(null, WHAT_STATE_LIFE)
        message.data = msg.data
        try {
            mMainMessenger?.send(message)
        }catch (e:RemoteException){
            e.printStackTrace()
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return mMessenger.binder
    }


}