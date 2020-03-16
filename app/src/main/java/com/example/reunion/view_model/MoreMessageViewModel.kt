package com.example.reunion.view_model

import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.User

class MoreMessageViewModel:BaseViewModel() {


    var realName = MutableLiveData("")
    var name = MutableLiveData("")
    var signature = MutableLiveData("")
    var province = MutableLiveData("")
    var city = MutableLiveData("")
    var district = MutableLiveData("")
    var sex = MutableLiveData(0)
    var birthday = MutableLiveData("")

    var header = MutableLiveData("")

    fun initUser(user: User.Data) {
        name.value = user.uName
        realName.value = user.uRealName
        header.value = user.uHeadPortrait
        province.value = user.uProvince
        city.value = user.uCity
        district.value = user.uDistrict
        sex.value = user.uSex
        birthday.value = user.uBirthday
        header.value = user.uHeadPortrait
        signature.value = user.uSignature
    }

    fun getSignatureString(signature:String?):String{
        if (signature == null)
            return ""
        if (signature.length>10){
            return signature.substring(0,8)+"…"
        }else
            return signature
    }

    fun getAreaString(province: String?, city: String?, district: String?): String {
        val builder = StringBuilder()
        if (province!=null&&province.isNotEmpty()) builder.append(province).append(" ")
        if (city!=null&&city.isNotEmpty()) builder.append(city).append(" ")
        if (district!=null&&district.isNotEmpty()) builder.append(district)
        return builder.toString()
    }

    fun getSexString(sex:Int) = when(sex){
        0 -> MyApplication.resource().getString(R.string.sex_0)
        1->MyApplication.resource().getString(R.string.sex_1)
        else->MyApplication.resource().getString(R.string.sex_2)
    }
}