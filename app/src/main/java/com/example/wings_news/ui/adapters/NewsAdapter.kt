package com.example.wings_news.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wings_news.R
import com.example.wings_news.data.model.Article
import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(
    private val context: Context,
    private val myCLick: MyClick
) : ListAdapter<Article, NewsAdapter.NewsHolder>(DIFF_ITEM_CALLBACK) {

    companion object {
        val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.items, parent, false)
        return NewsHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: NewsHolder, position: Int) {
        holder.bind(getItem(position))

        holder.singleItem.setOnClickListener {
            myCLick.onArticleClick(getItem(position))
        }
    }

    class NewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: AppCompatImageView = itemView.findViewById(R.id.image)
        private val source: AppCompatTextView = itemView.findViewById(R.id.tvSource)
        private val date: AppCompatTextView = itemView.findViewById(R.id.tvDate)
        private val title: AppCompatTextView = itemView.findViewById(R.id.tvTitle)
        val singleItem: CardView = itemView.findViewById(R.id.singleItems)

        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(article: Article) {
            source.text = article.source?.name
            date.text = "\u2022" + article?.publishedAt?.let { dateTime(it) }
            title.text = article.title

            Glide.with(itemView.context)
                .load(article.urlToImage)
                .into(image)

        }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun dateTime(t: String): String? {
            val prettyTime = PrettyTime(Locale(getCountry()))
            var time: String? = null
            try {
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:", Locale.ENGLISH)
                val date = simpleDateFormat.parse(t)
                time = prettyTime.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return time
        }

        private fun getCountry(): String? {
            val country = Locale.getDefault().country
            return country.toLowerCase()
        }

    }

    interface MyClick {
        fun onArticleClick(article: Article)
    }

}