package com.gxx.activitylifelibrary.model

import java.io.Serializable

class LifeModel() : Serializable {
    var position: Int = -1;//位置
    var processName: String = ""//进程名称
    var isCheckForeground: Boolean = false//是否去检查前后台，默认false，不检查
}