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
import com.gxx.activitylifelibrary.service.ActivityLifeService.Companion.WHAT_STATE_LIFE
import com.gxx.activitylifelibrary.util.ProcessUtils


/**
 * @date 创建时间: 2022/6/19
 * @auther gaoxiaoxiong
 * @description 监听前台后台配置
 **/
object ActivityLifeCallbackSdk : Application.ActivityLifecycleCallbacks {
    const val TAG = "LifeCall"

    private var mOnLifeCallBackListener: OnLifeCallBackListener? = null
    private var mIsMainProcess: Boolean = false//默认不是主进程
    private var mProcessName: String = ""
    private var mApplication: Application? = null
    private var mActivityCount = 0//activity 个数
    private var mPosition = 0//当前在哪个生命周期
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
     */
    fun init(
        isDebug: Boolean,
        application: Application,
        onLifeCallBackListener: OnLifeCallBackListener
    ) {
        application.registerActivityLifecycleCallbacks(this)
        this.mIsDebug = isDebug
        this.mApplication = application
        this.mProcessName = ProcessUtils.getProcessName(application.baseContext)//当前进程名称
        this.mIsMainProcess = mProcessName.equals(application.packageName)//当前是否为主进程
        this.mOnLifeCallBackListener = onLifeCallBackListener//接口回调
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
            if (msg.what == WHAT_STATE_LIFE && mIsMainProcess && mApplication != null && msg.data != null) {
                val model = msg.data.getSerializable(BUNDLE_MODEL) as LifeModel
                if (model == null) {
                    return
                }

                if (model.isCheckForeground){
                    val isForeground = isRunningForeground(mApplication!!.baseContext,model.processName)
                    mOnLifeCallBackListener?.onAppForeground(
                        isForeground,
                        model.processName,
                        LIST_LIFE_NAME[model.position]
                    )
                }

            }
        }
    })

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            mServiceMessenger = Messenger(p1)
            sendMessageToService(if (mIsMainProcess) WHAT_MAIN_INIT else WHAT_STATE_LIFE, mPosition,true);
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.e(TAG,"断开链接 = " + p0?.packageName)
            mServiceMessenger = null
        }
    }

    /**
     * 发送消息到服务器端
     * @param what WHAT_STATE_LIFE 处理生命周期
     * @param position 从LIST_LIFE_NAME取下标
     * @param activityCount activity 个数
     */
    fun sendMessageToService(what: Int, position: Int, isCheckForeground: Boolean) {
        this.mPosition = position
        if (mServiceMessenger == null) {
            Log.d(TAG, "serviceMessenger 还未初始化")
            return
        }
        val message = Message.obtain()
        //replyTo参数包含客户端Messenger
        message.replyTo = mReceiveMessenger
        message.what = what
        val model = LifeModel()
        model.position = position
        model.processName = mProcessName
        model.isCheckForeground = isCheckForeground
        val bundle = Bundle()
        bundle.putSerializable(BUNDLE_MODEL, model)
        message.data = bundle
        mServiceMessenger?.send(message)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        mOnLifeCallBackListener?.onActivityCreated(p0, p1)
        sendMessageToService(WHAT_STATE_LIFE, 0, false)
    }

    override fun onActivityStarted(p0: Activity) {
        mOnLifeCallBackListener?.onActivityStarted(p0)
        sendMessageToService(WHAT_STATE_LIFE, 1, false)
    }

    override fun onActivityResumed(p0: Activity) {
        mActivityCount = mActivityCount + 1
        mOnLifeCallBackListener?.onActivityResumed(p0)
        sendMessageToService(WHAT_STATE_LIFE, 2, true)
    }

    override fun onActivityPaused(p0: Activity) {
        mOnLifeCallBackListener?.onActivityPaused(p0)
        sendMessageToService(WHAT_STATE_LIFE, 3,  false)
    }

    override fun onActivityStopped(p0: Activity) {
        mActivityCount = mActivityCount - 1
        mOnLifeCallBackListener?.onActivityStopped(p0)
        sendMessageToService(WHAT_STATE_LIFE, 4, if (mActivityCount <= 0) true else false)

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        mOnLifeCallBackListener?.onActivitySaveInstanceState(p0, p1)
        sendMessageToService(WHAT_STATE_LIFE, 5,false)
    }

    override fun onActivityDestroyed(p0: Activity) {
        mOnLifeCallBackListener?.onActivityDestroyed(p0)
        sendMessageToService(WHAT_STATE_LIFE, 6,false)
    }

    /**
     * @date 创建时间: 2022/6/20
     * @auther gaoxiaoxiong
     * @description 检查是否在前台
     * return true前台
     **/
    fun isRunningForeground(context: Context,processName:String): Boolean {
        val activityManager =
            context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        for (appProcess in appProcesses) {
            if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcess.processName.equals(processName)) {
                    return true
                }
            }
        }
        return false
    }


}