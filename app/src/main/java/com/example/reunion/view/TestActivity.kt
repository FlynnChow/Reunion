package com.example.reunion.view

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.example.reunion.R
import com.example.reunion.customize.CommentView
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        testButton.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.view_comment_dialog,null,false)
            val dialog = Dialog(this)
            dialog.setContentView(view)
            val window = dialog.window
            val params = window?.attributes
            params?.width = ViewGroup.LayoutParams.MATCH_PARENT
            params?.height = ViewGroup.LayoutParams.MATCH_PARENT
            params?.y = -1000
            window?.attributes = params
            window?.decorView?.setBackgroundColor(Color.WHITE)
            window?.decorView?.setPadding(0,0,0,0)
            dialog.show()
        }
    }
}


