package com.gxx.activitylifelibrary

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Application
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.os.IBinder.DeathRecipient
import android.util.Log
import com.gxx.activitylifelibrary.inter.OnLifeCallBackListener
import com.gxx.activitylifelibrary.inter.OnLifeServiceLifeListener
import com.gxx.activitylifelibrary.model.LifeModel
import com.gxx.activitylifelibrary.service.ActivityLifeService
import com.gxx.activitylifelibrary.service.ActivityLifeService.Companion.BUNDLE_MODEL
import com.gxx.activitylifelibrary.service.ActivityLifeService.Companion.WHAT_LIFT_CHECK_AGAIN
import com.gxx.activitylifelibrary.service.ActivityLifeService.Companion.WHAT_MAIN_INIT
import com.gxx.activitylifelibrary.service.ActivityLifeService.Companion.WHAT_STATE_LIFE
import com.gxx.activitylifelibrary.util.LogUtil
import com.gxx.activitylifelibrary.util.ProcessUtils


/**
 * @date 创建时间: 2022/6/19
 * @auther gaoxiaoxiong
 * @description 监听前台后台配置
 **/
object ActivityLifeCallbackSdk : Application.ActivityLifecycleCallbacks {
    const val TAG = "LifeCall"
    private var mOnLifeCallBackListener: OnLifeCallBackListener? = null
    private var mOnLifeServiceLifeListener: OnLifeServiceLifeListener? = null;
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
        this.mIsMainProcess = mProcessName == application.packageName//当前是否为主进程
        this.mOnLifeCallBackListener = onLifeCallBackListener//接口回调
    }

    /**
     * @date 创建时间: 2022/6/22
     * @auther gaoxiaoxiong
     * @description 启动服务，用于多进程
     **/
    fun bindService(
        application: Application,
        onLifeServiceLifeListener: OnLifeServiceLifeListener
    ) {
        this.mOnLifeServiceLifeListener = onLifeServiceLifeListener
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
        @SuppressLint("HandlerLeak")
        override fun handleMessage(msg: Message) {
            if(mApplication == null || msg.data == null || !mIsMainProcess){
                return
            }
            if (msg.what == WHAT_STATE_LIFE) {
                val model = msg.data.getSerializable(BUNDLE_MODEL) as LifeModel
                if (model.isCheckForeground) {
                    if (LIST_LIFE_NAME[model.position] == NAME_ON_RESUMED) {
                        mOnLifeCallBackListener?.onProcessForeground(
                            true,
                            model.processName, true
                        )
                        mOnLifeCallBackListener?.onAppForeground(true)
                    } else {
                        val isForeground =
                            isRunningForeground(mApplication!!.baseContext, model.processName)
                        if (isForeground) {
                            //开启延迟操作,再次去检查一次，存在你退到桌面了，你还是APP还是在前台的判断
                            val newMessage = Message.obtain()
                            newMessage.what = ActivityLifeService.WHAT_LIFT_CHECK_AGAIN
                            val bundle = Bundle()
                            bundle.putSerializable(BUNDLE_MODEL, model)
                            newMessage.data = bundle
                            sendMessageDelayed(newMessage, 200)
                        } else {
                            checkMessengerRunningForeground(false,model)
                        }
                    }
                }
            } else if (msg.what == WHAT_LIFT_CHECK_AGAIN) {
                val model = msg.data.getSerializable(BUNDLE_MODEL) as LifeModel
                if(mIsDebug){
                  Log.d(TAG, "收到再次检查的回调")
                }
                val isForeground = isRunningForeground(mApplication!!.baseContext, model.processName)
                if(mIsDebug){
                  Log.d(TAG, "isForeground->"+isForeground)
                }
                checkMessengerRunningForeground(isForeground, model)
            }
        }
    })

    /**
     * @date 创建时间: 2022/11/1
     * @author gaoxiaoxiong
     * @description
     */
    private fun checkMessengerRunningForeground(isForeground:Boolean,lifeModel: LifeModel){
        if(mIsDebug){
            LogUtil.d(TAG,"checkMessengerRunningForeground->是否主进程->${mIsMainProcess}")
        }

        mOnLifeCallBackListener?.onProcessForeground(
            isForeground,
            lifeModel.processName, true
        )

        if(mApplication == null){
            return
        }

        if (!isForeground) {
            val foregroundName = getForegroundName(mApplication!!)
            if (foregroundName == null) {
                mOnLifeCallBackListener?.onAppForeground(false)
            } else {
                val listName = foregroundName.split(":")
                if (listName.isEmpty()) {
                    mOnLifeCallBackListener?.onAppForeground(false)
                } else {
                    if (listName[0] == mProcessName) {
                        mOnLifeCallBackListener?.onAppForeground(true)
                    }
                }
            }
        }
    }


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            mServiceMessenger = Messenger(p1)
            sendMessageToService(
                if (mIsMainProcess) WHAT_MAIN_INIT else WHAT_STATE_LIFE,
                mPosition,
                true
            );
            mOnLifeServiceLifeListener?.onBindLifeServiceSuccess()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.e(TAG, "断开链接 = " + p0?.packageName)
            mServiceMessenger = null
            mOnLifeServiceLifeListener?.onBindLifeServiceDisConnect()
        }
    }


    /**
     * 发送消息到服务器端
     * @param what WHAT_STATE_LIFE 处理生命周期
     * @param position 从LIST_LIFE_NAME取下标
     */
    fun sendMessageToService(what: Int, position: Int, isCheckForeground: Boolean) {
        this.mPosition = position
        if (mServiceMessenger == null) {
            Log.d(TAG, "serviceMessenger 还未初始化")
            return
        }

        if (!mServiceMessenger!!.binder.isBinderAlive){
            Log.d(TAG,"服务端已死亡，不将发送消息")
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
        model.activityCount = mActivityCount
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
        mActivityCount++
        if (mIsDebug) {
            Log.d(TAG, "processName->${mProcessName}，resumed-activityCount->$mActivityCount")
        }
        mOnLifeCallBackListener?.onActivityResumed(p0)
        //告诉主进程
        sendMessageToService(WHAT_STATE_LIFE, 2, true)
        //告诉自己的进程
        if (!mIsMainProcess) {
            mOnLifeCallBackListener?.onProcessForeground(true, mProcessName, false)
        }
    }

    override fun onActivityPaused(p0: Activity) {
        mActivityCount--
        mOnLifeCallBackListener?.onActivityPaused(p0)
        sendMessageToService(WHAT_STATE_LIFE, 3, false)
    }

    override fun onActivityStopped(activity: Activity) {
        if (mIsDebug) {
            Log.d(TAG, "processName->${mProcessName}，stopped->activityCount->$mActivityCount")
            Log.d(TAG, "processName->${mProcessName}，stopped->activityName->${activity.javaClass.canonicalName}")
        }
        mOnLifeCallBackListener?.onActivityStopped(activity)
        //告诉主进程
        sendMessageToService(WHAT_STATE_LIFE, 4, mActivityCount <= 0)
        //告诉自己的进程
        if (!mIsMainProcess && mActivityCount <= 0 && mOnLifeCallBackListener != null) {
            val isForeground = isRunningForeground(mApplication!!.baseContext, mProcessName)
            mOnLifeCallBackListener?.onProcessForeground(isForeground, mProcessName, false)
        }
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        mOnLifeCallBackListener?.onActivitySaveInstanceState(p0, p1)
        sendMessageToService(WHAT_STATE_LIFE, 5, false)
    }

    override fun onActivityDestroyed(p0: Activity) {
        mOnLifeCallBackListener?.onActivityDestroyed(p0)
        sendMessageToService(WHAT_STATE_LIFE, 6, false)
    }

    /**
     * @date 创建时间: 2022/6/20
     * @auther gaoxiaoxiong
     * @description 检查是否在前台
     * @param processName 传入的进程名称
     * return true前台
     **/
    fun isRunningForeground(context: Context, processName: String): Boolean {
        val activityManager =
            context.applicationContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        for (appProcess in appProcesses) {
            if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcess.processName.equals(processName)) {
                    if (mIsDebug) {
                        Log.d(
                            TAG,
                            "isRunningForeground->processName->isMainPrecess->$mIsMainProcess"
                        )
                        Log.d(TAG, "isRunningForeground->processName->$processName")
                    }
                    return true
                }
            }
        }
        return false
    }

    /**
     * @date 创建时间: 2022/6/22
     * @auther gaoxiaoxiong
     * @description 获取前台进程的名称
     **/
    fun getForegroundName(context: Context): String? {
        val activityManager =
            context.applicationContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses
        if (appProcesses == null) {
            return null
        } else {
            for (appProcess in appProcesses)
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return appProcess.processName
                }
        }
        return null;
    }

}