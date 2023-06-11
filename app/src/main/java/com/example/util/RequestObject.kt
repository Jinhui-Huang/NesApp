package com.example.util

import android.graphics.Bitmap

class RequestObject(
    val title: String,
    val lmodify: String,
    val source: String,
    val digest: String,
    val imgsrc: String,
    var imgBitmap: Bitmap? =null) {

    override fun toString(): String {
        return "RequestObject(title='$title', lmodify='$lmodify', source='$source', digest='$digest', imgsrc='$imgsrc')"
    }
}