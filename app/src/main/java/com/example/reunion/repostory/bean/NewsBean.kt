package com.example.reunion.repostory.bean

import android.os.Parcel
import android.os.Parcelable
import com.example.reunion.util.StringDealerUtil

class NewsBean {
    var status = 0
    var msg = ""
    var result: Result?= null

    public class Result{
        var num = 0
        var list:ArrayList<News> ?= null
    }

    public class News() :Parcelable{
        var title = ""
        var time = ""
        var src = ""
        var pic = ""
        var content = ""
        var url = ""

        constructor(parcel: Parcel) : this() {
            title = parcel.readString()?:""
            time = parcel.readString()?:""
            src = parcel.readString()?:""
            pic = parcel.readString()?:""
            content = parcel.readString()?:""
            url = parcel.readString()?:""
        }

        fun getId() = StringDealerUtil.getStringToMD5(url)
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(title)
            parcel.writeString(time)
            parcel.writeString(src)
            parcel.writeString(pic)
            parcel.writeString(content)
            parcel.writeString(url)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<News> {
            override fun createFromParcel(parcel: Parcel): News {
                return News(parcel)
            }

            override fun newArray(size: Int): Array<News?> {
                return arrayOfNulls(size)
            }
        }
    }
}