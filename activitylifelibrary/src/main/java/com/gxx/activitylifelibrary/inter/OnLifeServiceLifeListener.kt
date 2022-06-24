package com.gxx.activitylifelibrary.inter

interface OnLifeServiceLifeListener {
    /**
     * @date 创建时间: 2022/6/22
     * @auther gaoxiaoxiong
     * @description 绑定成功
     **/
    fun onBindLifeServiceSuccess()
    /**
     * @date 创建时间: 2022/6/22
     * @auther gaoxiaoxiong
     * @description 断开连接
     **/
    fun onBindLifeServiceDisConnect()
}