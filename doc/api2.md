## 1. 用户关注
### 路径: user/follow
### 内容：关注其他用户，以便优先获得其他用户发表的内容
### 请求参数：
uId: 关注者用户 id

targetUId: 被关注者用户 id

follow：Boolean : 0 表示关注 1 表示取消关注

### 响应

code：
* 200 成功
* 400 目标用户uid不存在

msg：信息

因为这个原子性操作有时会发生错误
如果follow = true(关注目标用户)，此时服务器记录的也是true(已经建立关注)，那么直接返回200:成功 就可以了
以此类推

## 2. 寻人寻物 收藏
### 路径: search/star
### 内容：收藏寻人或者寻物帖子
### 请求参数：
uId: 关注者用户 id

sId: 收藏的寻人/寻物 帖子的id

star：Boolean : 0 表示收藏 1 表示取消收藏

### 响应

code：
* 200 成功
* 400 异常，信息可以打印到msg上

msg：

因为这个原子性操作有时会发生错误

如果star = true，此时服务器记录的也是true(已经收藏)，那么直接返回200:成功 就可以了
以此类推


## 3. 寻人寻物 搜索
### 路径: home/search
### 内容：根据关键词对寻人/寻物的帖子进行模糊搜索
### 请求参数：
keyword:String,关键词

page:int，页数


### 响应参数：

code
* 200 成功
* 300 List为空
* 400 异常，信息可以打印到msg上

data List[FaceBean] 包含全部信息



## 4. 寻人寻物 删除
### 路径: search/delete
### 内容：对用户自己发表的寻人/寻物帖子进行删除
### 请求参数：
uId: 用户 id

sId: 目标寻人/寻物 帖子的id
### 响应

code：
* 200 成功
* 400 异常，信息可以打印到msg上

msg：


## 5. 用户 搜索
### 路径: user/search
### 内容：根据关键词对用户进行搜索(我觉得可以综合 uid，手机号，昵称和真实姓名四个条件)
### 请求参数：
keyword:String,关键词
page:int，页数

### 响应参数：

code
* 200 成功
* 300 List为空
* 400 异常，信息可以打印到msg上

data List[User] 包含用户可公开信息

msg


## 6. 关注 查询
### 路径: follow/search
### 内容：查询目标uid用户关注的所有用户的公开信息
### 请求参数：
uid:目标用户id

### 响应参数：

code
* 200 成功
* 300 List为空
* 400 异常，信息可以打印到msg上

data List[User] 包含用户可公开信息

msg


## 7. 被关注 查询
### 路径: fan/search
### 内容：查询关注目标uid用户的所有用户的公开信息
### 请求参数：
uid:目标用户id

### 响应参数：

code
* 200 成功
* 300 List为空
* 400 异常，信息可以打印到msg上

data List[User] 包含用户可公开信息

msg

## 8. 相互关注 查询
### 路径: friend/search
### 内容：查询关注目标uid用户的所有用户的公开信息
### 请求参数：
uid:目标用户id

### 响应参数：

code
* 200 成功
* 300 List为空
* 400 异常，信息可以打印到msg上

data List[User] 包含用户可公开信息

msg

## 9. 收藏 查询
### 路径: star/search
### 内容：查询目标用户收藏的所有寻人寻物
### 请求参数：
uid:目标用户id

### 响应参数：

code
* 200 成功
* 300 List为空
* 400 异常，信息可以打印到msg上

data List[FaceBean] 包含全部信息

msg


## 10. 是否收藏
### 路径: star/whether
### 内容：查询用户是否收藏某个 寻人/寻物 帖子
### 请求参数：

sId:寻人/寻物 id

uId:用户id

### 响应参数：

code
* 200 成功
* 400 异常，信息可以打印到msg上

data result:Boolean,true:收藏，false:没收藏

msg

## 11. 是否关注
### 路径: follow/whether
### 内容：查询用户是否收关注了某个用户
### 请求参数：
uId:用户id
targetUId:目标用户id

### 响应参数：

code
* 200 成功
* 400 异常，信息可以打印到msg上

data result:Boolean,true:关注，false:没关注

msg
