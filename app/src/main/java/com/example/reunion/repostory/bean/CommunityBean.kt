package com.example.reunion.repostory.bean

class CommunityBean {

    //公共的属性
    var mainId:String? = null // 主题的 id 主键

    var type:Int = 0 // 类型 0 -> 主题 1 -> 评论
    var header:String? = null // 头像URL
    var nickName:String? = null // 昵称
    var uId:String? = null // UID
    var time:String? = null // 时间

    //作为主题内容独有
    var content:String? = null // 内容文字
    var image:String? = null // 内容图片URL

    /**
     * 首次获取主题时候默认会包含主题的 1 - 3 楼的评论
     * 没有评论，则为null
     */
    var comments:ArrayList<CommunityBean>? = null

    //作为评论内容独有
    var cId:String? = null // 主题的 id 主键
    var comment:String? = null // 评论内容
    var toUId:String? = null // 被评论用户的UID,如果为null表面评论的是主题
    var toName:String? = null // 被评论用户的昵称,如果为null表面评论的是主题
    var floor:Int = 1 //评论的楼层 从 1 开始计数

    class Bean{
        var code:Int = 0
        var msg:String = ""
        var data:ArrayList<CommunityBean>? = null
    }
}