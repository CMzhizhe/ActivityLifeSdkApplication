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
     * @param isMainProcess 是否为主进程
     */
    fun onProcessForeground(isForeground:Boolean,processName: String,isMainProcess: Boolean){

    }

    /**
     * @date 创建时间: 2022/6/22
     * @auther gaoxiaoxiong
     * @description 当前APP是否在主进程，如果有一个子进程在，就算整个APP在主进程
     **/
    fun onAppForeground(isForeground:Boolean){

    }
}