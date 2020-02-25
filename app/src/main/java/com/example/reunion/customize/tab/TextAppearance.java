package com.example.reunion.customize.tab;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.content.res.Resources.NotFoundException;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Log;
import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StyleRes;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.RestrictTo.Scope;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.res.ResourcesCompat.FontCallback;
import com.google.android.material.R.styleable;
import com.google.android.material.resources.TextAppearanceConfig;

@RestrictTo({Scope.LIBRARY_GROUP})
public class TextAppearance {
    private static final String TAG = "TextAppearance";
    private static final int TYPEFACE_SANS = 1;
    private static final int TYPEFACE_SERIF = 2;
    private static final int TYPEFACE_MONOSPACE = 3;
    public final float textSize;
    @Nullable
    public final ColorStateList textColor;
    @Nullable
    public final ColorStateList textColorHint;
    @Nullable
    public final ColorStateList textColorLink;
    public final int textStyle;
    public final int typeface;
    @Nullable
    public final String fontFamily;
    public final boolean textAllCaps;
    @Nullable
    public final ColorStateList shadowColor;
    public final float shadowDx;
    public final float shadowDy;
    public final float shadowRadius;
    @FontRes
    private final int fontFamilyResourceId;
    private boolean fontResolved = false;
    @Nullable
    private Typeface font;

    public TextAppearance(Context context, @StyleRes int id) {
        TypedArray a = context.obtainStyledAttributes(id, styleable.TextAppearance);
        this.textSize = a.getDimension(styleable.TextAppearance_android_textSize, 0.0F);
        this.textColor = MaterialResources.getColorStateList(context, a, styleable.TextAppearance_android_textColor);
        this.textColorHint = MaterialResources.getColorStateList(context, a, styleable.TextAppearance_android_textColorHint);
        this.textColorLink = MaterialResources.getColorStateList(context, a, styleable.TextAppearance_android_textColorLink);
        this.textStyle = a.getInt(styleable.TextAppearance_android_textStyle, 0);
        this.typeface = a.getInt(styleable.TextAppearance_android_typeface, 1);
        int fontFamilyIndex = MaterialResources.getIndexWithValue(a, styleable.TextAppearance_fontFamily, styleable.TextAppearance_android_fontFamily);
        this.fontFamilyResourceId = a.getResourceId(fontFamilyIndex, 0);
        this.fontFamily = a.getString(fontFamilyIndex);
        this.textAllCaps = a.getBoolean(styleable.TextAppearance_textAllCaps, false);
        this.shadowColor = MaterialResources.getColorStateList(context, a, styleable.TextAppearance_android_shadowColor);
        this.shadowDx = a.getFloat(styleable.TextAppearance_android_shadowDx, 0.0F);
        this.shadowDy = a.getFloat(styleable.TextAppearance_android_shadowDy, 0.0F);
        this.shadowRadius = a.getFloat(styleable.TextAppearance_android_shadowRadius, 0.0F);
        a.recycle();
    }

    @VisibleForTesting
    @NonNull
    public Typeface getFont(Context context) {
        if (this.fontResolved) {
            return this.font;
        } else {
            if (!context.isRestricted()) {
                try {
                    this.font = ResourcesCompat.getFont(context, this.fontFamilyResourceId);
                    if (this.font != null) {
                        this.font = Typeface.create(this.font, this.textStyle);
                    }
                } catch (NotFoundException | UnsupportedOperationException var3) {
                } catch (Exception var4) {
                    Log.d("TextAppearance", "Error loading font " + this.fontFamily, var4);
                }
            }

            this.createFallbackTypeface();
            this.fontResolved = true;
            return this.font;
        }
    }

    public void getFontAsync(Context context, final TextPaint textPaint, @NonNull final FontCallback callback) {
        if (this.fontResolved) {
            this.updateTextPaintMeasureState(textPaint, this.font);
        } else {
            this.createFallbackTypeface();
            if (context.isRestricted()) {
                this.fontResolved = true;
                this.updateTextPaintMeasureState(textPaint, this.font);
            } else {
                try {
                    ResourcesCompat.getFont(context, this.fontFamilyResourceId, new FontCallback() {
                        public void onFontRetrieved(@NonNull Typeface typeface) {
                            TextAppearance.this.font = Typeface.create(typeface, TextAppearance.this.textStyle);
                            TextAppearance.this.updateTextPaintMeasureState(textPaint, typeface);
                            TextAppearance.this.fontResolved = true;
                            callback.onFontRetrieved(typeface);
                        }

                        public void onFontRetrievalFailed(int reason) {
                            TextAppearance.this.createFallbackTypeface();
                            TextAppearance.this.fontResolved = true;
                            callback.onFontRetrievalFailed(reason);
                        }
                    }, null);
                } catch (NotFoundException | UnsupportedOperationException var5) {
                } catch (Exception var6) {
                    Log.d("TextAppearance", "Error loading font " + this.fontFamily, var6);
                }

            }
        }
    }

    private void createFallbackTypeface() {
        if (this.font == null) {
            this.font = Typeface.create(this.fontFamily, this.textStyle);
        }

        if (this.font == null) {
            switch(this.typeface) {
                case 1:
                    this.font = Typeface.SANS_SERIF;
                    break;
                case 2:
                    this.font = Typeface.SERIF;
                    break;
                case 3:
                    this.font = Typeface.MONOSPACE;
                    break;
                default:
                    this.font = Typeface.DEFAULT;
            }

            if (this.font != null) {
                this.font = Typeface.create(this.font, this.textStyle);
            }
        }

    }

    public void updateDrawState(Context context, TextPaint textPaint, FontCallback callback) {
        this.updateMeasureState(context, textPaint, callback);
        textPaint.setColor(this.textColor != null ? this.textColor.getColorForState(textPaint.drawableState, this.textColor.getDefaultColor()) : -16777216);
        textPaint.setShadowLayer(this.shadowRadius, this.shadowDx, this.shadowDy, this.shadowColor != null ? this.shadowColor.getColorForState(textPaint.drawableState, this.shadowColor.getDefaultColor()) : 0);
    }

    public void updateMeasureState(Context context, TextPaint textPaint, @Nullable FontCallback callback) {
        if (TextAppearanceConfig.shouldLoadFontSynchronously()) {
            this.updateTextPaintMeasureState(textPaint, this.getFont(context));
        } else {
            this.getFontAsync(context, textPaint, callback);
            if (!this.fontResolved) {
                this.updateTextPaintMeasureState(textPaint, this.font);
            }
        }

    }

    public void updateTextPaintMeasureState(@NonNull TextPaint textPaint, @NonNull Typeface typeface) {
        textPaint.setTypeface(typeface);
        int fake = this.textStyle & ~typeface.getStyle();
        textPaint.setFakeBoldText((fake & 1) != 0);
        textPaint.setTextSkewX((fake & 2) != 0 ? -0.25F : 0.0F);
        textPaint.setTextSize(this.textSize);
    }
}

