package com.gxx.activitylifelibrary.service

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import com.gxx.activitylifelibrary.ActivityLifeCallbackSdk
import com.gxx.activitylifelibrary.ActivityLifeCallbackSdk.NAME_ON_CREATE
import com.gxx.activitylifelibrary.ActivityLifeCallbackSdk.NAME_ON_STARTED
import com.gxx.activitylifelibrary.ActivityLifeCallbackSdk.NAME_ON_STOPPED
import com.gxx.activitylifelibrary.model.LifeModel
import com.gxx.activitylifelibrary.util.LogUtil

class ActivityLifeService : Service() {
    companion object {
        const val WHAT_MAIN_NOINIT = -1//不需要初始化
        const val WHAT_MAIN_INIT = 0 //主的初始化
        const val WHAT_STATE_LIFE = 1//处理生命周期的

        const val BUNDLE_MODEL = "model"
    }
    private var mActivityCount = 1;
    private var mMainMessenger: Messenger? = null;
    private val mMessenger = Messenger(object : Handler() {
        override fun handleMessage(msg: Message) {
            LogUtil.d("msg.what=" + "${msg.what}")
            if (msg.what == WHAT_MAIN_INIT && msg.replyTo != null) {
                mMainMessenger = msg.replyTo;
            } else if (mMainMessenger != null && msg.what == WHAT_STATE_LIFE) {
                if (msg.data == null) {
                    LogUtil.d("msg.data为null")
                    return
                }
                val model = msg.data.getSerializable(BUNDLE_MODEL) as LifeModel
                if (ActivityLifeCallbackSdk.LIST_LIFE_NAME[ model.position].equals(NAME_ON_CREATE)){
                    mActivityCount = mActivityCount + 1;
                }else if (ActivityLifeCallbackSdk.LIST_LIFE_NAME[ model.position].equals(NAME_ON_STOPPED)){
                    mActivityCount = mActivityCount - 1;
                }
                model.count = mActivityCount
                if (ActivityLifeCallbackSdk.mIsDebug){
                    LogUtil.d("name = " + ActivityLifeCallbackSdk.LIST_LIFE_NAME[ model.position])
                    LogUtil.d("processName = " + model.processName)
                    LogUtil.d("count = " + model.count)
                }
                val message = Message.obtain(null, WHAT_STATE_LIFE)
                message.data = msg.data
                try {
                    mMainMessenger?.send(message)
                }catch (e:RemoteException){
                    e.printStackTrace()
                }
            }
        }
    })

    override fun onBind(p0: Intent?): IBinder? {
        return mMessenger.binder
    }


}