# 重逢圈
## 重逢圈Bean类
```
class CommunityBean {

    var communityId:String? = null // 主题的 id 主键
    var header:String? = null // 头像URL
    var nickName:String? = null // 昵称
    var uId:String? = null // UID
    var time:Long = 0L // 时间 ->System.currentTimeMillis()

    var content:String? = null // 内容文字
    var locate:String? = null // 位置信息 [省市]
    var images:ArrayList<String>? = null // 内容图片URL

    /**
     * 首次获取主题时候默认会包含主题的 1 - 3 楼的评论
     * 没有评论，则为null
     */
    var comments:ArrayList<Comment>? = null
    var commentNum = 0 //评论总数
}
```
## 重逢圈 评论Bean类(没有父子评论之分)
```
class Comment{
        var communityId:String? = null // 主题的id
        var header:String? = null // 头像URL
        var nickName:String? = null // 昵称
        var uId:String? = null // UID
        var time:Long = 0L // 时间 ->System.currentTimeMillis()

        var cId:String? = null //  评论的id 主键
        var comment:String? = null // 评论内容

        var toUId:String? = null // 被评论用户的UID,如果为null表面评论的是主题
        var toName:String? = null // 被评论用户的昵称,如果为null表面评论的是主题
        var floor:Int = 1 //评论的楼层 从 1 开始计数
}
```
## 1. 发布重逢圈
### POST (community/sendMain)
### 请求参数
* uId(String) 发布用户uid
* locate(String) 发布用户位置信息，可能是null
* content(String) 发布的内容
* file{Int}(文件字节流) 发布的图片，数量范围在 0 - 9
1. file0:第一张图片

2. file1:第二张图片

3. file2:第三张图片

**以此类推**
### 响应参数
#### 响应Bean类
```
class ResponseBean{
        var code:Int = 0
        var msg:String = ""
        var data:CommunityBean? = null
    }
```
* code 
1. 200:代表请求失败
2. 其他:请求失败，将失败信息加在msg上
* msg 请求的结果一些异常信息
* data 发布好的重逢帖子


## 2. 发布重逢圈 评论
### GET (community/sendComment)
### 请求参数
* communityId(String) 请求目标帖子的id
* uId(String) 发布用户uid
* toUId(String) 发布用户如果评论的是主题，它的值是null，否则代表评论的是某个评论，它的值是那个评论用户的uid
* comment(String) 评论的内容

### 响应参数
#### 响应Bean类
```
class CommentBean{
        var code:Int = 0
        var msg:String = ""
        var data:Comment? = null
    }
```
* code 
1. 200:代表请求失败
2. 其他:请求失败，将失败信息加在msg上
* msg 请求的结果的一些异常信息
* data 发布好的重逢评论内容

## 3. 获取重逢圈关注用户发布的内容
### GET (community/obtainFollow)
### 请求参数
* uId(String) 请求用户的uid
* page(Int) 页码，从 1 开始

## 4. 获取重逢圈推荐(按热度)内容
### GET (community/obtainRecommend)
### 请求参数
* page(Int) 页码，从 1 开始

## 5. 获取重逢圈 某个用户的发布内容
### GET (community/obtainUser)
### 请求参数
* uId(String) 目标用户的uid
* page(Int) 页码，从 1 开始

#### 以上三个API的响应Bean类
```
class ResponseBeans{
        var code:Int = 0
        var msg:String = ""
        var data:ArrayList<CommunityBean>? = null
    }
```

* code 
1. 200:代表请求失败
2. 300:请求内容为空，已经没有内容了
3. 其他:请求失败，将失败信息加在msg上
* msg 请求的结果一些异常信息
* data 发布好的重逢帖子

## 6. 获取重逢圈 获取评论内容
### GET (community/obtainComment)
### 请求参数
* communityId(String) 请求目标帖子的id
* page(Int) 页码，从 1 开始

#### 获取评论的Bean类
```
class CommentBeans{
        var code:Int = 0
        var msg:String = ""
        var data:ArrayList<Comment>? = null
    }
```
* code 
1. 200:代表请求失败
2. 300:请求内容为空，已经没有内容了
3. 其他:请求失败，将失败信息加在msg上
* msg 请求的结果一些异常信息
* data 发布好的重逢帖子
