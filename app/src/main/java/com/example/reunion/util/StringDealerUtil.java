package com.example.reunion.util;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.reunion.MyApplication;
import com.example.reunion.R;
import com.xujiaji.happybubble.BubbleDialog;
import com.xujiaji.happybubble.BubbleLayout;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Random;

import Decoder.BASE64Encoder;

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

    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String ImageToBase64(String imgPath) {
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(imgPath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        // 返回Base64编码过的字节数组字符串
        return encoder.encode(Objects.requireNonNull(data));
    }



    public static String getPinYinString(String chines) {
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(nameChar[i]);
            }
        }
        return sb.toString();
    }

    public static void showBubbleDialog(Activity activity, View view,View targetView){
//        BubbleLayout bl = new BubbleLayout(activity);
//        bl.setBubbleColor(Color.BLUE);
//        bl.setShadowColor(Color.RED);
//        new BubbleDialog(activity)
//                .addContentView(view)
//                .setClickedView(targetView)
//                .setPosition(BubbleDialog.Position.BOTTOM)
//                .calBar(true)
//                .setBubbleLayout(bl)
//                .show();

        new BubbleDialog(activity)
                .addContentView(LayoutInflater.from(activity).inflate(R.layout.dialog_bubble, null))
                .setClickedView(targetView)
                .calBar(true)
                .show();
    }

    public static String getDeviceId(Context context){
        StringBuilder sb = new StringBuilder();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            TelephonyManager phoneManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                String DEVICE_ID = phoneManager.getDeviceId();
                if (DEVICE_ID != null){
                    sb.append(DEVICE_ID);
                }
            } catch (Exception e) { }
        }
        sb.append(getAndroidId());
        return getStringToMD5(sb.toString());
    }

    public static int length(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }

    public static Boolean isChinese(String str){
        String chinese = "[\u0391-\uFFE5]";
        return str.matches(chinese);
    }

}
