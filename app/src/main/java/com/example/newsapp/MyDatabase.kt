package com.example.newsapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabase(val context: Context, dbName: String, version: Int):
    SQLiteOpenHelper(context, dbName, null, version){
    private val createBook = "create table News(" +
            "id integer primary key autoincrement," +
            "title text," +
            "content text," +
            "author text," +
            "date test," +
            "imageId integer)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createBook)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }
}