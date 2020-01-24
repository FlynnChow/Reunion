package com.example.reunion.base;

import android.annotation.SuppressLint;
import android.app.Application;
import androidx.annotation.NonNull;

public abstract class BaseApplicationViewModel extends BaseViewModel {
    @SuppressLint("StaticFieldLeak")
    private Application mApplication;

    public BaseApplicationViewModel(@NonNull Application application) {
        mApplication = application;
    }

    /**
     * Return the application.
     */
    @SuppressWarnings({"TypeParameterUnusedInFormals", "unchecked"})
    @NonNull
    public <T extends Application> T getApplication() {
        return (T) mApplication;
    }
}
