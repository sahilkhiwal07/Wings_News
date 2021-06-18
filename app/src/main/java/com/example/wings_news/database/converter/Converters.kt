package com.example.wings_news.database.converter

import androidx.room.TypeConverter
import com.example.wings_news.data.model.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source) : String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }

}