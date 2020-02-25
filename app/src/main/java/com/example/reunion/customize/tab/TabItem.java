package com.example.reunion.customize.tab;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.widget.TintTypedArray;
import com.google.android.material.R.styleable;

public class TabItem extends View {
    public final CharSequence text;
    public final Drawable icon;
    public final int customLayout;

    public TabItem(Context context) {
        this(context, null);
    }

    public TabItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, styleable.TabItem);
        this.text = a.getText(styleable.TabItem_android_text);
        this.icon = a.getDrawable(styleable.TabItem_android_icon);
        this.customLayout = a.getResourceId(styleable.TabItem_android_layout, 0);
        a.recycle();
    }
}
