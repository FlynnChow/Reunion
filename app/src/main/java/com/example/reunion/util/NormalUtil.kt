package com.example.reunion.util

import android.util.Log
import java.util.regex.Pattern

object NormalUtil {
    fun isMobile(phone:String?):Boolean{
        if (phone==null||phone.length != 11)
            return false
        for (index in 0..1){
            val num = phone[index]
            if (index == 0&&num == '1')
                continue
            else if (num=='3'||num=='5'||num=='7'||num=='8')
                continue
            return false
        }
        return true
    }


}