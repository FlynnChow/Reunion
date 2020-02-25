package com.example.reunion.view

import android.content.pm.ActivityInfo
import android.os.Bundle
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : BaseActivity() {


    override fun create(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_test)

        testButton.setOnClickListener {
            Matisse.from(this)
                .choose(MimeType.ofAll())
                .countable(true)
                .maxSelectable(9)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(GlideEngine())
                .showPreview(false) // Default is `true`
                .forResult(1)
        }
    }
}


