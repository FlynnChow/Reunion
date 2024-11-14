# 重逢(Reunion)
![](https://github.com/CappuccinoZero/Reunion/blob/master/app/src/main/res/mipmap-xxxhdpi/logo_r.png)https://github.com/FlynnChow/Reunion/blob/master/README.md

**Create by : 2020-01-09**

**Author : [FlynnChow](https://github.com/CappuccinoZero)**

**我与商学院，经贸学院，机械学院的同学合作开发的基于C/S架构的移动端应用**
## 简介
本项目为一款面向社会各类群体、以公益性为目的、以“重逢”为主题，帮助用户寻找失散多年的知己故友、找回被拐儿童妇女等类的公益类社交APP。
致力于打造一个服务社会群众、信息真实且有保障的公益社交寻人平台，打破以往常规的网络寻人启事，采用人脸智能识别的方法，按匹配相似度的高低进行筛选，并保障基本的隐私信息。
## 下载
[开发版-2020-3-30](https://reunion.yulinzero.xyz/download/reunion.apk)
## 视频展示
* 暂无
## 图片展示
![](https://github.com/CappuccinoZero/Reunion/blob/master/image/image1.jpg)

![](https://github.com/CappuccinoZero/Reunion/blob/master/image/image2.jpg)

![](https://github.com/CappuccinoZero/Reunion/blob/master/image/image3.jpg)

![](https://github.com/CappuccinoZero/Reunion/blob/master/image/image4.jpg)

## 开发环境
* Android Studio 3.6
* JDK-1.8
* API Level 19 - 29
* Kotlin
## 项目架构
**MVVM**
* DataBinding
* LiveData
* ViewModel
* Lifecycle
* Kotlin Coroutines

**Remote Model**
* Retrofit2
* OkHttp3 and WebSocket

**Local Model**
* Room
* SharePreferences
* FireBase ML Kit

**Camera**
* Camera2
* CameraX(alpha10)

**Other**
* WorkManager
* Glide3.7
* Matisse
* GaussianBlur

  **...**
## 开发进度
* 2020-01-08：构建仓库
* 2020-01-10：项目架构设计和依赖引入
* 2020-01-24：基类设计
* 2020-01-27：登录视图
* 2020-01-29：登录和注册
* 2020-02-06：文件上传等工具封装
* 2020-02-13：首页布局以及新闻，目前无法很好解决屏幕旋转数据丢失问题
* 2020-02-17：新闻及评论
* 2020-02-19：个人资料及滑动冲突初步解决
* 2020-02-29：识别，Camera2 and CameraX，人脸录入视图逻辑
* 2020-03-09：社区,寻人,与我的发布
* 2020-03-16：完成即时通讯和系统推送以及几乎全部的视图逻辑
* 2020-03-17：App第一版开发完毕->修复bug
* 2020-03-18：修复新闻图片不显示的问题
* 2020-03-22：更新大图加载和保存(最后)
## 开发状态
* 首次开发结束
## 还未解决的问题
* Camera还存在机型兼容问题
* 还存在未知Bug
****
# License
```
Copyright 2020 FlynnChow

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
