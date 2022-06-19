package com.gxx.lifenoticeapplication

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.gxx.activitylifelibrary.ActivityLifeCallbackSdk
import com.gxx.activitylifelibrary.ActivityLifeCallbackSdk.NAME_ON_CREATE
import com.gxx.activitylifelibrary.ActivityLifeCallbackSdk.NAME_ON_STARTED
import com.gxx.activitylifelibrary.ActivityLifeCallbackSdk.NAME_ON_STOPPED
import com.gxx.activitylifelibrary.ActivityLifeCallbackSdk.TAG
import com.gxx.activitylifelibrary.inter.OnLifeCallBackListener
import com.gxx.lifenoticeapplication.utils.MLUtils

class MyApplication : Application(), OnLifeCallBackListener {
    override fun onCreate() {
        super.onCreate()
        val processName = MLUtils.getProcessName(this.applicationContext)
        ActivityLifeCallbackSdk.init(BuildConfig.DEBUG,processName.equals(this.packageName),processName,this,this)
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
     * @param isForeground true 是的
     * @param processName 进程名称
     * @param lifeName 生命周期的名字
     */
    override fun onAppForeground(isForeground: Boolean, processName: String, lifeName: String) {
        if (lifeName.equals(NAME_ON_STOPPED)){
            Log.e(TAG,"isForeground = " + isForeground)
            Log.e(TAG,"processName = " + processName)
            Log.e(TAG,"lifeName = " + lifeName)
        }else if (lifeName.equals(NAME_ON_CREATE)){
            Log.e(TAG,"isForeground = " + isForeground)
            Log.e(TAG,"processName = " + processName)
            Log.e(TAG,"lifeName = " + lifeName)
        }
    }



}