package com.example.wings_news.data.network

import com.example.wings_news.data.model.News
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("top-headlines")
    suspend fun getHeadlines(
        @Query("country") country: String,
        @Query("page") pageNumber: Int,
        @Query("apiKey") apiKey: String
    ) : Response<News>

    @GET("everything")
    suspend fun searchForNews(
        @Query("q") searchNews: String,
        @Query("page") pageNumber: Int,
        @Query("apiKey") apiKey: String
    ): Response<News>

}