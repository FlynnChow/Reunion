package com.example.reunion.repostory.bean

import com.example.reunion.util.StringDealerUtil

class NewsBean {
    var status = 0
    var msg = ""
    var result: Result?= null

    public class Result{
        var num = 0
        var list:ArrayList<News> ?= null
    }

    public class News{
        var title = ""
        var time = ""
        var src = ""
        var pic = ""
        var content = ""
        var url = ""

        fun getId() = StringDealerUtil.getStringToMD5(url)
    }
}