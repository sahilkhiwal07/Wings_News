package com.example.wings_news.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.wings_news.data.model.Article
import com.example.wings_news.database.converter.Converters
import com.example.wings_news.database.articleDao.NewsDao

@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class NewsDatabase: RoomDatabase() {
    abstract fun getNewsDao(): NewsDao

    companion object {
        @Volatile
        private var instance: NewsDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): NewsDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java, "news_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

            }
            return instance!!
        }
    }

}