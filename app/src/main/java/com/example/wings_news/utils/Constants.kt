package com.example.wings_news.utils

import java.util.*

class Constants {

    companion object{
        const val BASE_URL = "https://newsapi.org/v2/"
        const val API_KEY = "2cd83409506a4c59ae497f4374559ac3"
        const val SEARCH_TIME_DELAY = 300L
        const val QUERY_PAGE_SIZE = 20
        val COUNTRY by lazy { getCountry() }

        private fun getCountry(): String {
            val country = Locale.getDefault().country
            return country.toLowerCase(Locale.ROOT)
        }
    }

}