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