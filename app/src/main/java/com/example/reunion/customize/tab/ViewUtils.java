package com.example.reunion.customize.tab;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.annotation.SuppressLint;
import android.graphics.PorterDuff.Mode;
import android.view.View;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import androidx.core.view.ViewCompat;

@RestrictTo({Scope.LIBRARY_GROUP})
public class ViewUtils {
    public ViewUtils() {
    }

    public static Mode parseTintMode(int value, Mode defaultMode) {
        switch(value) {
            case 3:
                return Mode.SRC_OVER;
            case 4:
            case 6:
            case 7:
            case 8:
            case 10:
            case 11:
            case 12:
            case 13:
            default:
                return defaultMode;
            case 5:
                return Mode.SRC_IN;
            case 9:
                return Mode.SRC_ATOP;
            case 14:
                return Mode.MULTIPLY;
            case 15:
                return Mode.SCREEN;
            case 16:
                return Mode.ADD;
        }
    }

    @SuppressLint("WrongConstant")
    public static boolean isLayoutRtl(View view) {
        return ViewCompat.getLayoutDirection(view) == 1;
    }
}

