# 重逢圈 BEAN

## 重逢圈主题内容

```
    var communityId:String? = null // 主题的 id 主键
    var type:Int = 0 // 类型 0 -> 主题 1 -> 评论
    var header:String? = null // 头像URL
    var nickName:String? = null // 昵称
    var uId:String? = null // UID
    var time:String? = null // 时间

    var content:String? = null // 内容文字
    
    var images:ArrayList<String>? = null // 内容图片URL//图片数量 0 - 9

    /**
     * 首次获取主题时候默认会包含主题的 1 - 3 楼的评论
     * 没有评论，则为null
     */
    var comments:ArrayList<Comment>? = null
    var commentNum = 0 //评论总数
```

## 评论
**评论无主次之分**

```
class Comment{
        var communityId:String? = null // 主题的id
        var type:Int = 0 // 类型 0 -> 主题 1 -> 评论
        var header:String? = null // 头像URL
        var nickName:String? = null // 昵称
        var uId:String? = null // UID
        var time:String? = null // 时间

        var cId:String? = null //  评论的id 主键
        var comment:String? = null // 评论内容

        var toUId:String? = null // 被评论用户的UID,如果为null表面评论的是主题
        var toName:String? = null // 被评论用户的昵称,如果为null表面评论的是主题
        var floor:Int = 1 //评论的楼层 从 1 开始计数

        fun getCommentContent():String{
            if (toName == null)
                toName = ""
            else
                toName = "@${toName}:"
            return "${toName}${comment}"
        }
    }
```

# 消息

## 即时通讯信息
```
@Entity(tableName = "im_message")
data class ImMessageBean(
    @PrimaryKey(autoGenerate = true) var tableId:Long? = null   //这是本地数据库主键，服务器不必接收
){
    //聊天id : uid1.append(uid2) [uid1 < uid2]
    var imId:String? = null

    //time :System.currentTimeMillis
    //由客户端生成
    var time:Long? = null

    //发出消息用户的uid
    var uId:String? = null

    //接收消息用户的uid
    var toUid:String? = null

    //聊天信息
    var content:String? = null

    //默认true 由客户端控制
    var isRead:Boolean? = null

    //发送消息时 发送者的头像URL
    var header:String? = null

    //发送消息时 发送者的昵称
    var nickName:String? = null
    
    //用于记录客户端是否发送成功，服务器需要接收
    var sendSuccess:Boolean = false
}
```

## 系统信息
```
@Entity(tableName = "system_message")
data class SystemMessageBean(
    @PrimaryKey(autoGenerate = true) var tableId:Long? = null  //这是本地数据库主键，服务器不必接收
){
    //消息类型
    var type:Int = 0

    //time :System.currentTimeMillis
    //由客户端生成
    var time:Long? = null

    //消息 目标用户的uid
    var targetUid:String? = null

    //消息标题
    var title:String? = null

    //消息内容
    var content:String? = null

    //是否已经被读，服务器默认true
    var isRead:Boolean? = null

    //目标用户的头像URL
    var header:String? = null

    //目标用户的昵称
    var nickName:String? = null

}
```
