package com.example.reunion.repostory.bean

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import com.example.reunion.util.StringDealerUtil
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter
import kotlin.collections.ArrayList

class CommentResult {
    var code = 0
    var msg = ""
    var data:CommentBean.Comment ?= null
}