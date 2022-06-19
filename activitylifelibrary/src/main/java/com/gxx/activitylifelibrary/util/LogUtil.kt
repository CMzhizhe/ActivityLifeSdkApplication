package com.gxx.activitylifelibrary.util

import android.util.Log
import com.gxx.activitylifelibrary.ActivityLifeCallbackSdk

class LogUtil {
    companion object {
        const val TAG = "LifeCall"
        fun d(tag: String, msg: String) {
            if (ActivityLifeCallbackSdk.mIsDebug){
                Log.d(tag, msg)
            }
        }

        fun d(msg: String) {
            this.d(TAG, msg)
        }
    }
}