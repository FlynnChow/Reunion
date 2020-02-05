package com.example.reunion.util;

import android.provider.Settings;
import android.util.Log;

import com.example.reunion.MyApplication;

import java.security.Key;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;

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
        Log.d("测试id",id);
        return (id == null?"":id);
    }

    private static String getTimeFormat(Long time){
        SimpleDateFormat format = new SimpleDateFormat("MM*dd*yyyy&&mm#HH#ss");
        return format.format(time);
    }
}
