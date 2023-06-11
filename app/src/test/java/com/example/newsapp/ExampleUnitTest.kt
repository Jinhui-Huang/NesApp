package com.example.newsapp

import com.example.util.RequestObject
import com.example.util.Utils
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val url = Utils.getUrl()
        var jsonData = Utils.getJsonData(url)
        var js = JsonParser().parse(jsonData).asJsonObject
        var jsArray = js.getAsJsonArray("T1348647853363")
        jsonData = jsArray.toString()

        val gson = Gson()
        val typeOf = object : TypeToken<List<RequestObject>>() {}.type
        val newsList = gson.fromJson<List<RequestObject>>(jsonData, typeOf)
        newsList.forEach {
            if (it.digest.isNotEmpty()) {
                println(it.toString())
            }
        }
    }
}