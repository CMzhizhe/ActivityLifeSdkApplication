package com.gxx.activitylifelibrary.inter

import android.app.Activity
import android.os.Bundle

interface OnLifeCallBackListener {
    fun onActivityCreated(activity: Activity, bundle: Bundle?) {
    }

    fun onActivityStarted(activity: Activity) {

    }

    fun onActivityResumed(activity: Activity) {

    }

    fun onActivityPaused(activity: Activity) {

    }

    fun onActivityStopped(activity: Activity) {

    }

    fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {

    }

    fun onActivityDestroyed(activity: Activity) {

    }

    /**
     * App是否在前台
     * @param isForeground true 是的
     * @param processName 进程名称
     * @param lifeName 生命周期的名字
     */
    fun onAppForeground(isForeground:Boolean,processName: String,lifeName:String)

}