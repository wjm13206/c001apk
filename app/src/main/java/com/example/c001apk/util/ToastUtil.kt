package com.example.c001apk.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.c001apk.MyApplication

fun makeToast(text: String) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(MyApplication.context, text, Toast.LENGTH_SHORT).show()
    }
}

object ToastUtil {
    fun toast(context: Context, msg: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context.applicationContext, msg, Toast.LENGTH_SHORT).show()
        }
    }
}