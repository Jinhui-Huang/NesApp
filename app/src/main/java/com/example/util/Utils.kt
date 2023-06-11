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