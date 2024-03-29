package com.gxx.lifenoticeapplication

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.gxx.activitylifelibrary.ActivityLifeCallbackSdk
import com.gxx.activitylifelibrary.ActivityLifeCallbackSdk.TAG
import com.gxx.activitylifelibrary.inter.OnLifeCallBackListener
import com.gxx.activitylifelibrary.inter.OnLifeServiceLifeListener
import com.gxx.activitylifelibrary.util.ProcessUtils

class MyApplication : Application(), OnLifeCallBackListener, OnLifeServiceLifeListener {
    override fun onCreate() {
        super.onCreate()
        ActivityLifeCallbackSdk.init(BuildConfig.DEBUG,this,this)
        ActivityLifeCallbackSdk.bindService(this,this)
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        super.onActivityCreated(activity, bundle)
    }

    override fun onActivityPaused(activity: Activity) {
        super.onActivityPaused(activity)
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
        Log.e(TAG,"isForeground = " + isForeground)
        Log.e(TAG,"processName = " + processName)
        Log.e(TAG,"============================")
    }

    /**
     * @date 创建时间: 2022/6/22
     * @auther gaoxiaoxiong
     * @description 当前APP是否在主进程，如果有一个子进程在，就算整个APP在主进程
     **/
    override fun onAppForeground(isForeground: Boolean) {
        Log.e(TAG,"整个APP是否在前台 = " + isForeground)
    }

    /**
     * @date 创建时间: 2022/6/22
     * @auther gaoxiaoxiong
     * @description 绑定成功
     **/
    override fun onBindLifeServiceSuccess() {
        Log.d(TAG,"lifeService:${ProcessUtils.getProcessName(this)},绑定成功")
    }

    /**
     * @date 创建时间: 2022/6/22
     * @auther gaoxiaoxiong
     * @description 断开连接
     **/
    override fun onBindLifeServiceDisConnect() {
        Log.d(TAG,"lifeService:${ProcessUtils.getProcessName(this)},断开连接")
    }

}