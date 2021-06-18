package com.example.wings_news.database.repository

import android.app.Application
import com.example.wings_news.data.model.Article
import com.example.wings_news.data.network.RetrofitInstance
import com.example.wings_news.database.articleDao.NewsDao
import com.example.wings_news.database.db.NewsDatabase
import com.example.wings_news.utils.Constants.Companion.API_KEY
import com.example.wings_news.utils.Constants.Companion.COUNTRY

class NewsRepository(application: Application) {
    private val newsDao: NewsDao

    init {
        val db = NewsDatabase.getDatabase(application)
        newsDao = db.getNewsDao()
    }

    fun getAllNews() = newsDao.getAllNews()

    suspend fun insertArticle(article: Article) = newsDao.insertArticle(article)

    suspend fun deleteArticle(article: Article) = newsDao.deleteArticle(article)

    // APi call
    suspend fun getBreakingNews(page: Int) = RetrofitInstance.api.getHeadlines(COUNTRY, page, API_KEY)

    suspend fun searchForNews(search: String, page: Int) = RetrofitInstance.api.searchForNews(search, page, API_KEY)

}