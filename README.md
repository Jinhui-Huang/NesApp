# 前言
本工程来自学校Android Studio课程的一次小作业, 应用到的知识非常基础, 
本人知识有限, 只能写出个大概怎么实现的, 对于一些深层原理的实现还是一知半解,如有编写错误欢迎指出

# 一. 前期工作准备

## 1. 设置好国内镜像源

在这个文件下配置好gradle的镜像源, 这样下载依赖包和插件就会变快[settings.gradle](settings.gradle)

```gradle
pluginManagement {
    repositories {
        maven {url 'https://maven.aliyun.com/repository/public/'}
        maven {url 'https://maven.aliyun.com/repository/google/'}
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {url 'https://maven.aliyun.com/repository/public/'}
        maven {url 'https://maven.aliyun.com/repository/google/'}
        google()
        mavenCentral()
    }
}
```

## 2. 配置相关依赖, 这个工程下需要的一些依赖

在这个文件下进行配置[app/build.gradle](app/build.gradle)

```gralde
dependencies {
    implementation 'androidx.core:core-ktx:1.7.0'
    implementation 'androidx.appcompat:appcompat:1.4.1'
    implementation 'com.google.android.material:material:1.5.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.3'
    implementation 'androidx.recyclerview:recyclerview:1.3.0'
    implementation 'com.squareup.okhttp3:okhttp:4.1.0'
    implementation 'com.google.code.gson:gson:2.8.5'

    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
}
```

重要的是这三个依赖

```
    implementation 'androidx.recyclerview:recyclerview:1.3.0'
    implementation 'com.squareup.okhttp3:okhttp:4.1.0'
    implementation 'com.google.code.gson:gson:2.8.5'
```

## 3. 网络权限以及接口所用的API
网络权限提前开启以免后期忘记开启无法使用网络调取API

首先在这个文件夹下[app/src/main/res/xml](app/src/main/res/xml)新建一个文件`network_security_config.xml`, 内容为:
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true"/>
</network-security-config>
```
然后在权限文件[app/src/main/AndroidManifest.xml](app/src/main/AndroidManifest.xml)里进行修改
- 添加手机访问权限
- 添加网络访问的许可文件路径
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    
    <!--添加手机访问权限-->
    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        
        <!--添加网络访问的许可文件路径(粘贴时把这段注释删掉)-->
        android:networkSecurityConfig="@xml/network_security_config"
        
        android:theme="@style/Theme.NewsApp"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```
API接口地址:

`API_URL = "http://c.m.163.com/nc/article/headline/T1348647853363/0-40.html"`

# 二. 主界面的layout文件编写
xml文件在这[app/src/main/res/layout/activity_main.xml](app/src/main/res/layout/activity_main.xml)

主要是一些主界面的ui设计
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="#DD4CC0EF"
        android:orientation="horizontal">
        <ImageView
            android:layout_width="50dp"
            android:layout_height="50dp"
            android:background="@drawable/newsimg"/>
        <TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:textColor="#FFFFFF"
            android:gravity="center"
            android:textSize="30sp"
            android:textStyle="bold"
            android:text="延大趣闻---微博分博"
            app:layout_constraintBottom_toBottomOf="parent" />



    </LinearLayout>
    <TextView
        android:layout_width="match_parent"
        android:layout_height="10dp"
        android:background="#DD4CC0EF"/>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#DAD9D9">

    </androidx.recyclerview.widget.RecyclerView>

</LinearLayout>
```
手机上的效果
![](手机ui.jpg)

# 3. 传进RecycleView的layout编写
因为传统的ListView性能太差了, 这里采用RecycleView的控件, 
在这个文件夹下[app/src/main/res/layout](app/src/main/res/layout)编写传进RecycleView的layout,
新建一个xml文件`news_layout.xml`

单个新闻条的ui设计如下:
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="190dp"
    xmlns:app="http://schemas.android.com/apk/res-auto"

    android:layout_marginLeft="5dp"
    android:layout_marginTop="5dp"
    android:layout_marginRight="5dp"
    android:background="@drawable/border"
    android:orientation="vertical">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="75dp"
        android:orientation="horizontal">


        <ImageView
            android:id="@+id/news_image"
            android:layout_width="75dp"
            android:layout_height="75dp"
            android:layout_gravity="center"
            android:background="@drawable/biankuang"
            android:paddingTop="5dp"
            android:paddingLeft="5dp"
            android:paddingRight="3dp"
            android:src="@drawable/yanda"/>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical">

            <TextView
                android:id="@+id/news_title"
                android:layout_width="match_parent"
                android:layout_height="45dp"
                android:paddingTop="3dp"
                android:text="新闻标题"
                android:textColor="@color/black"
                android:textSize="16sp"
                android:textStyle="bold" />

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="30dp"
                android:orientation="horizontal">

                <TextView
                    android:id="@+id/news_author"
                    android:layout_width="130dp"
                    android:layout_height="30dp"
                    android:paddingLeft="3dp"
                    android:text="新闻作者"
                    android:textColor="#8C8C8C"
                    android:textSize="12sp" />

                <TextView
                    android:id="@+id/news_date"
                    android:layout_width="match_parent"
                    android:layout_height="30dp"
                    android:text="日期"
                    android:textColor="#939292"
                    android:textSize="12sp" />
            </LinearLayout>
        </LinearLayout>
    </LinearLayout>

    <TextView
        android:id="@+id/news_content"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="10dp"
        android:layout_marginBottom="10dp"
        android:layout_marginRight="5dp"
        android:layout_marginLeft="5dp"
        android:scrollbars="vertical"
        android:text="新闻内容"
        android:textColor="#000000"
        android:textSize="18sp" />

</LinearLayout>
```
# 4. 主程序代码编写
ui设计好后需要主代码绑定进行使用, 在[MainActivity](app/src/main/java/com/example/newsapp/MainActivity.kt)下进行编写,
但是编写主代码前需要编写传进RecyclerView空间的Adapter, Adapter用来实现news_layout.xml的实现

在[app/src/main/java/com/example/newsapp](app/src/main/java/com/example/newsapp)先新建两个文件, 一个news对象文件`News.kt`用来实现新闻对象各个属性的包装,
一个adapter文件`NewsAdapter.kt`用来编写逻辑代码

- News.kt
```kotlin
package com.example.newsapp

import android.graphics.Bitmap

class News(
    val title: String,
    val content: String,
    val author: String,
    val date: String,
    val image: Bitmap
) {
}
```
- NewsAdapter.kt
```kotlin
package com.example.newsapp

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NewsAdapter(val newsList: List<News>, val context: Context) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.news_title)
        val content: TextView = view.findViewById(R.id.news_content)
        val author: TextView = view.findViewById(R.id.news_author)
        val date: TextView = view.findViewById(R.id.news_date)
        val image: ImageView = view.findViewById(R.id.news_image)
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.news_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: NewsAdapter.ViewHolder, position: Int) {
            val news = newsList[position]
            if (news != null){
                holder.title.text = news.title
                holder.author.text = news.author
                holder.content.text = news.content
                holder.content.movementMethod = ScrollingMovementMethod.getInstance()
                holder.date.text = news.date
                holder.image.setImageBitmap(news.image)
            }
        }

        override fun getItemCount(): Int {
            return newsList.size
        }
    }
```
接下来进行主程序的代码编写, 用到了上边的adapter

```kotlin
class MainActivity : AppCompatActivity() {
    lateinit var recyclerview: RecyclerView
    lateinit var myDatabase: MyDatabase

    private val newsData = ArrayList<News>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerview = findViewById(R.id.recyclerview)
        myDatabase = MyDatabase(this, "NewsBook.db", 1)
        //initView() //后期加上API接口的方法
        val adapter = NewsAdapter(newsData, this)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter


    }
}
```

# 5. 网络API接口的连接和JSON数据解析
这些我都写在了一个工具类里, 用来简化主程序MainActivity代码的编写
工具类在这[app/src/main/java/com/example/util/Utils.kt](app/src/main/java/com/example/util/Utils.kt)
```kotlin
package com.example.util

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.HttpURLConnection

object Utils {
    const val API_URL = "http://c.m.163.com/nc/article/headline/T1348647853363/0-40.html"

    lateinit var connection: HttpURLConnection

    fun getUrl() : String  {
        return StringBuilder().append(API_URL).toString()
    }


    fun getJsonData(url: String) : String {
        val client = OkHttpClient()
        val request =
            Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val jsonData = response.body?.string()
        return jsonData!!
    }

    fun getMessage(data: String) :List<RequestObject> {
        var jsonObject = JsonParser().parse(data).asJsonObject
        var jsArray = jsonObject.getAsJsonArray("T1348647853363")
        var data = jsArray.toString()

        val gson = Gson()
        val typeOf = object : TypeToken<List<RequestObject>>() {}.type
        val newsList = gson.fromJson<List<RequestObject>>(data, typeOf)
        /*newsList.forEach {
            if (it.digest.isNotEmpty()) {
                println(it.toString())
            }
        }*/
        return newsList
    }
}
```
API的连接使用了第三方工具okhttp, 
json数据的解析使用了第三方工具gson, 解析方式是先获取到对象数组, 将对象数组里的数据进行字符串处理, 再利用对象格式解析字符串, 获取到对象数组

# 6. 主程序中initView()方法的编写
调用工具类实现上方API接口的整合
```kotlin
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

    private val newsData = ArrayList<News>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerview = findViewById(R.id.recyclerview)
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
```
上述代码实现中有一个图片url获取网络资源转化为bitmap数据的实现
```
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
```
在这段代码中先是进行了网络url获取图片的输入流数据`val imgSource = BufferedInputStream(url.openStream())`

然后将获取到的数据利用bitmap工厂方法, 造出img的bitmap数据`it.imgBitmap = BitmapFactory.decodeStream(imgSource)`

最后添加到新闻对象中, 最终在adapter显示到页面ui中`holder.image.setImageBitmap(news.image)`

# 7. 整体效果的实现如下
![](NewsFinal.jpg)