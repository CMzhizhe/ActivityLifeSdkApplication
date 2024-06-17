# ActivityLifeSdkApplication
记录APP是在前台还是在后台，可用于多进程判断

### 使用教程

###### 1、添加依赖

根build.gradle
```
maven { url 'https://jitpack.io' }
```
app的build.gradle
```
  implementation 'com.github.CMzhizhe:ActivityLifeSdkApplication:1.0.5'
```

###### 2、配置清单文件
```
        <!--配置servic-->
        <service android:name="com.gxx.activitylifelibrary.service.ActivityLifeService" android:exported="false" android:process=":tools"/>
```

###### 3、修改application
```
class MyApplication : Application(), OnLifeCallBackListener, OnLifeServiceLifeListener {
    override fun onCreate() {
        super.onCreate()
	 ActivityLifeCallbackSdk.Companion.Builder()
            .setApplication(this)
            .setIsDebug(true)
            .setOnLifeCallBackListener(this)
            .setOnLifeServiceLifeListener(this)//设置该项的作用，假如有3个进程，ABC,A是主进程，B、C是子进程，如果APP在同时启动A、B、C的情况下，此时进程已经到了C了，然后A想知道我整个APP是否在前后台。如果不设置该项，A就无法得知B、C情况
            .build()
            .init()
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
```
