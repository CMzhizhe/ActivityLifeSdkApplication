package com.gxx.activitylifelibrary.util;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import androidx.annotation.NonNull;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * @author : gaoxiaoxiong
 * @date :2019/10/10 0010
 **/
public class ProcessUtils {


    public static boolean isMainProcess(Context context) {
        String processName = getProcessName(context);
        return processName != null && processName.equals(context.getPackageName());
    }


    /**
     * 判断应用是否已经启动
     *
     * @param context     上下文对象
     * @param packageName 要判断应用的包名
     * @return boolean
     */
    public static boolean isAppAlive(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < processInfos.size(); i++) {
            if (processInfos.get(i).processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @date :2019/10/22 0022
     * @author : gaoxiaoxiong
     * @description: 判断某个service是否在运行
     * return true 表示在运行
     * className ： service全路径名称
     **/
    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> info = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (info == null || info.size() == 0) return false;
        for (ActivityManager.RunningServiceInfo aInfo : info) {
            if (className.equals(aInfo.service.getClassName())) return true;
        }
        return false;
    }


    /**
     * @date :2019/11/21 0021
     * @author : gaoxiaoxiong
     * @description:获取进程名称
     **/
    public static String getProcessName(Context context) {
        String currentProcessName = null;
        if (context == null) {
            try {
                File file = new File("/proc/" + Process.myPid() + "/" + "cmdline");
                BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
                currentProcessName = mBufferedReader.readLine().trim();
                mBufferedReader.close();
                return currentProcessName;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        //1)通过Application的API获取当前进程名
        currentProcessName = getCurrentProcessNameByApplication();
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName;
        }

        //2)通过反射ActivityThread获取当前进程名
        currentProcessName = getCurrentProcessNameByActivityThread();
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName;
        }

        //3)通过ActivityManager获取当前进程名
        currentProcessName = getCurrentProcessNameByActivityManager(context);
        return currentProcessName;
    }

    /**
     * 通过Application新的API获取进程名，无需反射，无需IPC，效率最高。
     */
    private static String getCurrentProcessNameByApplication() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName();
        }
        return null;
    }

    /**
     * 通过反射ActivityThread获取进程名，避免了ipc
     */
    private static String getCurrentProcessNameByActivityThread() {
        String processName = null;
        try {
            final Method declaredMethod = Class.forName("android.app.ActivityThread", false, Application.class.getClassLoader())
                    .getDeclaredMethod("currentProcessName", (Class<?>[]) new Class[0]);
            declaredMethod.setAccessible(true);
            final Object invoke = declaredMethod.invoke(null, new Object[0]);
            if (invoke instanceof String) {
                processName = (String) invoke;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return processName;
    }

    /**
     * 通过ActivityManager 获取进程名，需要IPC通信
     */
    private static String getCurrentProcessNameByActivityManager(@NonNull Context context) {
        int pid = Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> runningAppList = am.getRunningAppProcesses();
            if (runningAppList != null) {
                for (ActivityManager.RunningAppProcessInfo processInfo : runningAppList) {
                    if (processInfo.pid == pid) {
                        return processInfo.processName;
                    }
                }
            }
        }
        return null;
    }





}
