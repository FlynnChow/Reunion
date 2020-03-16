package com.example.reunion.repostory.bean

import android.util.Log

class MessageBean() {
    constructor(msg:String):this(){
        this.msg = msg
        type = 0
    }
    /**
     * @description 类型
     * 0: 心跳机制
     * 1：单聊消息(单个)
     * 2：单聊消息(多个)
     * 3：系统消息(单个)
     * 4：系统消息(多个)
     */
    var type = 0

    var msg:String? = null //文本信息

    var imMessage:ImMessageBean? = null //type == 1时，只有单个聊天消息

    var systemMessage:SystemMessageBean? = null //type == 3时，只有单个系统消息

    var imBeansArray:ArrayList<ImBeans>? = null //type == 2时，有多个聊天消息

    var systemMessageArray:ArrayList<SystemMessageBean>? = null //type == 4时，有多个系统消息

    class ImBeans{
        var imId:String? = null //聊天的 imID，array 中信息的imID都等于它

        val array:ArrayList<ImMessageBean>? = null //按照 time:Long 生序排序
    }
}