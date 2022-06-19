package com.gxx.activitylifelibrary

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Application
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import com.gxx.activitylifelibrary.inter.OnLifeCallBackListener
import com.gxx.activitylifelibrary.model.LifeModel
import com.gxx.activitylifelibrary.service.ActivityLifeService
import com.gxx.activitylifelibrary.service.ActivityLifeService.Companion.BUNDLE_MODEL
import com.gxx.activitylifelibrary.service.ActivityLifeService.Companion.WHAT_MAIN_INIT
import com.gxx.activitylifelibrary.service.ActivityLifeService.Companion.WHAT_MAIN_NOINIT
import com.gxx.activitylifelibrary.service.ActivityLifeService.Companion.WHAT_STATE_LIFE
import com.gxx.activitylifelibrary.util.LogUtil

/**
 * @date 创建时间: 2022/6/19
 * @auther gaoxiaoxiong
 * @description 监听前台后台配置
 **/
object ActivityLifeCallbackSdk : Application.ActivityLifecycleCallbacks {
    const val TAG = "LifeCall"

    private var mOnLifeCallBackListener: OnLifeCallBackListener? = null
    private var mIsMainProcess: Boolean = false//默认不是主进程
    private var mProcessName: String = "";
    private var mApplication: Application? = null;
    var mIsDebug = false;

    const val NAME_ON_CREATE = "onActivityCreated"
    const val NAME_ON_STARTED = "onActivityStarted"
    const val NAME_ON_RESUMED = "onActivityResumed"
    const val NAME_ON_PAUSED = "onActivityPaused"
    const val NAME_ON_STOPPED = "onActivityStopped"
    const val NAME_ON_SAVE_INSTANCE_STATE = "onActivitySaveInstanceState"
    const val NAME_ON_DESTROYED = "onActivityDestroyed"
    val LIST_LIFE_NAME = mutableListOf<String>(
        NAME_ON_CREATE,
        NAME_ON_STARTED,
        NAME_ON_RESUMED,
        NAME_ON_PAUSED,
        NAME_ON_STOPPED,
        NAME_ON_SAVE_INSTANCE_STATE,
        NAME_ON_DESTROYED
    )

    /**
     * 初始化，注册生命周期回调函数
     * @param isDebug 是的在debug模式
     * @param onLifeCallBackListener 生命周期回调
     * @param isMainProcess 是否主进程，如果不是主进程，拿到生命周期后，会主动回调给主进程
     * @param processName 当前的进程名称
     */
    fun init(
        isDebug: Boolean,
        isMainProcess: Boolean,
        processName: String,
        application: Application,
        onLifeCallBackListener: OnLifeCallBackListener
    ) {
        application.registerActivityLifecycleCallbacks(this)
        this.mIsDebug = isDebug
        this.mApplication = application
        this.mOnLifeCallBackListener = onLifeCallBackListener
        this.mIsMainProcess = isMainProcess
        this.mProcessName = processName
        application.bindService(
            Intent(application, ActivityLifeService::class.java), serviceConnection,
            Service.BIND_AUTO_CREATE
        )
    }

    /**
     * 用于向service端发送消息的Messenger
     */
    private var mServiceMessenger: Messenger? = null;

    /**
     * 用于接收service发送的消息的Messenger
     */
    private val mReceiveMessenger = Messenger(object : Handler() {
        override fun handleMessage(msg: Message) {
            LogUtil.d("进程名称：" + mProcessName)
            if (msg.what == WHAT_STATE_LIFE && mIsMainProcess && mApplication != null && msg.data != null) {
                val model = msg.data.getSerializable(BUNDLE_MODEL) as LifeModel
                if (model == null) {
                    return
                }
                if (model.count <= 0) {
                    val isBack = isBackground(mApplication!!.baseContext)
                    mOnLifeCallBackListener?.onAppForeground(!isBack, model.processName, LIST_LIFE_NAME[model.position])
                }else{
                    mOnLifeCallBackListener?.onAppForeground(true, model.processName, LIST_LIFE_NAME[model.position])
                }
            }
        }
    })

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            mServiceMessenger = Messenger(p1)
            sendMessageToService(if (mIsMainProcess) WHAT_MAIN_INIT else WHAT_MAIN_NOINIT, -1);
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mServiceMessenger = null
        }
    }

    /**
     * 发送消息到服务器端
     * @param what WHAT_STATE_LIFE 处理生命周期
     * @param position 从LIST_LIFE_NAME取下标
     */
    fun sendMessageToService(what: Int, position: Int) {
        if (mServiceMessenger == null) {
            Log.d(TAG, "serviceMessenger 还未初始化")
            return
        }
        val message = Message.obtain()
        //replyTo参数包含客户端Messenger
        message.replyTo = mReceiveMessenger
        message.what = what
        if (position >= 0) {
            val model = LifeModel()
            model.position = position
            model.processName = mProcessName
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_MODEL, model)
            message.data = bundle
        }
        mServiceMessenger?.send(message)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        mOnLifeCallBackListener?.onActivityCreated(p0, p1)
        sendMessageToService(WHAT_STATE_LIFE, 0)
    }

    override fun onActivityStarted(p0: Activity) {
        mOnLifeCallBackListener?.onActivityStarted(p0)
        sendMessageToService(WHAT_STATE_LIFE, 1)
    }

    override fun onActivityResumed(p0: Activity) {
        mOnLifeCallBackListener?.onActivityResumed(p0)
        sendMessageToService(WHAT_STATE_LIFE, 2)
    }

    override fun onActivityPaused(p0: Activity) {
        mOnLifeCallBackListener?.onActivityPaused(p0)
        sendMessageToService(WHAT_STATE_LIFE, 3)
    }

    override fun onActivityStopped(p0: Activity) {
        mOnLifeCallBackListener?.onActivityStopped(p0)
        sendMessageToService(WHAT_STATE_LIFE, 4)
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        mOnLifeCallBackListener?.onActivitySaveInstanceState(p0, p1)
        sendMessageToService(WHAT_STATE_LIFE, 5)
    }

    override fun onActivityDestroyed(p0: Activity) {
        mOnLifeCallBackListener?.onActivityDestroyed(p0)
        sendMessageToService(WHAT_STATE_LIFE, 6)
    }


    /**
     * 判断程序是否在后台
     * @return true 是的，在后台
     */
   private fun isBackground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses
        for (appProcess in appProcesses) {
            if (appProcess.processName == context.getPackageName()) {
                return if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    true
                } else {
                    false
                }
            }
        }
        return false
    }


}