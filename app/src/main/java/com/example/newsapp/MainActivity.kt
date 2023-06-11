package com.example.newsapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.util.RequestObject
import com.example.util.Utils
import java.io.BufferedInputStream
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    lateinit var recyclerview: RecyclerView
    lateinit var myDatabase: MyDatabase

    private val newsData = ArrayList<News>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerview = findViewById(R.id.recyclerview)
        myDatabase = MyDatabase(this, "NewsBook.db", 1)
        initView()
        val adapter = NewsAdapter(newsData, this)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter


    }

    @SuppressLint("Range")
    private fun initView() {
        thread {
            try {
                val jsonData = Utils.getJsonData(Utils.getUrl())
                val newsList = Utils.getMessage(jsonData)
                Log.d(TAG, "传入数组")
                //可关闭数据写入
                initImage(newsList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initImage(newsList: List<RequestObject>) {
        thread {
            newsList.forEach {
                if (it.digest.isNotEmpty()) {
                    val url = URL(it.imgsrc)
                    val imgSource = BufferedInputStream(url.openStream())
                    it.imgBitmap = BitmapFactory.decodeStream(imgSource)
                    newsData.add(News(it.title, it.digest, it.source, it.lmodify, it.imgBitmap!!))
                    Log.d(TAG, it.toString())
                }
            }
            runOnUiThread {
                recyclerview.adapter = NewsAdapter(newsData, this)
            }
        }


    }

    companion object {
        const val TAG = "MainActivity"
    }
}