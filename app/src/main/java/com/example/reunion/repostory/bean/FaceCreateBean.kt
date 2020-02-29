package com.example.reunion.repostory.bean

class FaceCreateBean {
    var ret = 0
    var msg = ""
    var data:Data? = null

    class Data{
        var person_id = ""

        var face_id = ""

        var group_ids:ArrayList<String>? = null
    }
}