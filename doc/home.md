* 每页的数量由服务器决定，就不做为参数了，服务器可以动态改变它

## 1. 关注
### 路径: home/follow
### 内容：根据用户的uid获取，他关注的人发表的说说，以发布时间为顺序，内容混合寻人和寻物
### 请求参数：
uId:用户id

page:页数

### 响应参数：

#code (200 成功,300 结果为空，400 uid不存在)

#data List[FaceBean] 包含全部信息

## 2. 推荐
### 路径: home/recommend
### 内容：按照是热度获取说说，内容混合寻人和寻物
### 请求参数：

page:页数

### 响应参数：

#code (200 成功,300 结果为空）

#data List[FaceBean] 包含全部信息

## 3. 附近
### 路径: home/recommend
### 内容：按照地点获取说说，内容混合寻人和寻物
### 请求参数：

city:市

page:页数

### 响应参数：

#code (200 成功,300 结果为空）

#data List[FaceBean] 包含全部信息

## 4. 寻人
### 路径: home/people
### 内容：按照发布时间顺序和类型说说
### 请求参数：

time:失联时间，string，以年为单位

age:年龄，string，范围，“0-5”，[start-end）

区域：province:省 ， city:市 ， district:区

page:页数

* 因为时间，年龄，区域。这些条件可能部分是null，所以看情况，都没有就单纯按照时间顺序

### 响应参数：

#code (200 成功,300 结果为空）

#data List[FaceBean] 包含全部信息

## 5. 寻物
### 路径: home/body
### 内容：按照发布时间顺序和类型说说
### 请求参数：


time:失联时间，string，以年为单位

age:年龄，string，范围，“0-5”，[start-end）

区域：province:省 ， city:市 ， district:区

page:页数

* 因为时间，年龄，区域。这些条件可能部分是null，所以看情况，都没有就单纯按照时间顺序

### 响应参数：

#code (200 成功,300 结果为空）

#data List[FaceBean] 包含全部信息
