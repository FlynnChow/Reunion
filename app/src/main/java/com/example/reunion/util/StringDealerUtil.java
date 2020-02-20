package com.example.reunion.util;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.reunion.MyApplication;
import com.lljjcoder.bean.ProvinceBean;

import java.security.Key;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class StringDealerUtil {
    /**
     *
     * @param uid 用户ID
     */
    @Deprecated
    public static String getEncryptString(Long uid){
        String str = "*&" + uid + "&*" + getAndroidId() + "**&&";
        char[] hex = new char[] { '0', '1', '2', '3', '4', '5',
                '6', '7' , '8', '9', 'A', 'B', 'C', 'D', 'E','F' };
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] digest = md.digest();
            int length = digest.length;
            char[] chars = new char[length * 2];
            int num = 0;
            for (byte bt : digest) {
                chars[num++] = hex[bt >>> 4 & 0xf];
                chars[num++] = hex[bt & 0xf];
            }
            return new String(chars);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getAndroidIDKey(){
        String str = "FsHysS" + getAndroidId() + "p5qXVMoa";
        char[] hex = new char[] { '0', '1', '2', '3', '4', '5',
                '6', '7' , '8', '9', 'A', 'B', 'C', 'D', 'E','F' };
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] digest = md.digest();
            int length = digest.length;
            char[] chars = new char[length * 2];
            int num = 0;
            for (byte bt : digest) {
                chars[num++] = hex[bt >>> 4 & 0xf];
                chars[num++] = hex[bt & 0xf];
            }
            String md5Encode = new String(chars);
            int len = md5Encode.length();
            int traget = len / 8;
            if (traget<1) traget = 1;
            StringBuilder sb = new StringBuilder();
            for (int i=0;i<len;i+=traget){
                sb.append(md5Encode.charAt(i));
            }
            return sb.toString().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getStringToMD5(String str){
        char[] hex = new char[] { '0', '1', '2', '3', '4', '5',
                '6', '7' , '8', '9', 'A', 'B', 'C', 'D', 'E','F' };
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] digest = md.digest();
            int length = digest.length;
            char[] chars = new char[length * 2];
            int num = 0;
            for (byte bt : digest) {
                chars[num++] = hex[bt >>> 4 & 0xf];
                chars[num++] = hex[bt & 0xf];
            }
            return new String(chars);
        } catch (Exception e) {
            return null;
        }
    }

    private static String getAndroidId(){
        String id = Settings.Secure.getString(MyApplication.getApp().getContentResolver(),Settings.Secure.ANDROID_ID);
        return (id == null?"":id);
    }

    private static String getTimeFormat(Long time){
        SimpleDateFormat format = new SimpleDateFormat("MM*dd*yyyy&&mm#HH#ss");
        return format.format(time);
    }


}
