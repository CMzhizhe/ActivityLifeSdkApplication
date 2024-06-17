package com.gxx.lifenoticeapplication

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.gxx.activitylifelibrary.ActivityLifeCallbackSdk
import com.gxx.activitylifelibrary.ActivityLifeCallbackSdk.Companion.TAG
import com.gxx.activitylifelibrary.inter.OnLifeCallBackListener
import com.gxx.activitylifelibrary.inter.OnBindServiceLifeListener
import com.gxx.activitylifelibrary.util.ProcessUtils

class MyApplication : Application(), OnLifeCallBackListener, OnBindServiceLifeListener {
    override fun onCreate() {
        super.onCreate()
        ActivityLifeCallbackSdk.Companion.Builder()
            .setApplication(this)
            .setIsDebug(true)
            .setOnLifeCallBackListener(this)
            .setOnLifeServiceLifeListener(this)//设置该项目的作用，假如有3个进程，ABC,A是主进程，B、C是子进程，如果APP在同时启动A、B、C的情况下，A想知道我整个APP是否在前后台。如果不设置该项目，A就无法得知B、C情况
            .build()
            .init()
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        super.onActivityCreated(activity, bundle)
    }

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        super.onActivityPaused(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        super.onActivityStopped(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        super.onActivityDestroyed(activity)
    }


    /**
     * App是否在前台
     * 如果当前是主进程，这里是可以知道所有子进程，包括自己的接口回调
     * 如果当前是子进程，这里只会知道自己的是否在前台
     * @param isForeground true 是的
     * @param processName 进程名称
     * @param isMainProcess 是否为主进程
     */
    override fun onProcessForeground(isForeground: Boolean, processName: String,isMainProcess: Boolean) {
        Log.e(TAG,"============================")
        Log.e(TAG, "进程名称->processName = $processName" +
                        "->isForeground = $isForeground，" +
                        "是否主进程回调->${isMainProcess}，" +
                        "回调的进程名称->${ProcessUtils.getProcessName(this)}")
        Log.e(TAG,"============================")
    }

    /**
     * @date 创建时间: 2022/6/22
     * @auther gaoxiaoxiong
     * @description 当前APP是否在前台，如果有一个子进程在前台，就算整个APP在前台，只有主进程才能收到回调
     **/
    override fun onAppForeground(isForeground: Boolean) {
        Log.e(TAG, "整个APP是否在前台->$isForeground，当前进程名称->${ProcessUtils.getProcessName(this)}")
    }

    /**
     * @date 创建时间: 2022/6/22
     * @auther gaoxiaoxiong
     * @description 绑定成功
     **/
    override fun onBindLifeServiceSuccess() {
        Log.d(TAG,"lifeService->${ProcessUtils.getProcessName(this)},绑定成功")
    }

    /**
     * @date 创建时间: 2022/6/22
     * @auther gaoxiaoxiong
     * @description 断开连接
     **/
    override fun onBindLifeServiceDisConnect() {
        Log.d(TAG,"lifeService->${ProcessUtils.getProcessName(this)},断开连接")
    }

}