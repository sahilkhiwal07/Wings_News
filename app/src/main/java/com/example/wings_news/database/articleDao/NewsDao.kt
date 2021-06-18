package com.example.wings_news.database.articleDao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wings_news.data.model.Article

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: Article): Long

    @Query("SELECT * FROM Article")
    fun getAllNews(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

}