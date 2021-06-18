package com.example.wings_news.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.wings_news.data.model.Article
import com.example.wings_news.data.model.News
import com.example.wings_news.database.repository.NewsRepository
import com.example.wings_news.utils.Constants.Companion.COUNTRY
import com.example.wings_news.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel constructor(application: Application) : AndroidViewModel(application) {

    private val repository = NewsRepository(application)
    private val updateArticleProgressBar = MutableLiveData<Boolean>()

    val breakingNews: MutableLiveData<Resource<News>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: News? = null

    val searchNews: MutableLiveData<Resource<News>> = MutableLiveData()
    var searchNewsPage = 1

    init {
        getBreakingNews()
    }

    // Api Call
    fun getBreakingNews() = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        updateArticleProgressBar.value = true
        val newsResponse = repository.getBreakingNews(breakingNewsPage)
        breakingNews.postValue(handleNewsResponse(newsResponse))
        updateArticleProgressBar.value = false
    }

    fun getSearchNews(search: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        updateArticleProgressBar.value = true
        val searchResponse = repository.searchForNews(search, searchNewsPage)
        searchNews.postValue(handleSearchResponse(searchResponse))
        updateArticleProgressBar.value = false
    }

    private fun handleNewsResponse(response: Response<News>): Resource<News> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticle = breakingNewsResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticle?.addAll(newArticle)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchResponse(response: Response<News>): Resource<News> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    // Room Storage
    fun getAllNews() = repository.getAllNews()

    fun saveArticle(article: Article) = viewModelScope.launch {
        repository.insertArticle(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteArticle(article)
    }

    // Article Progress Bar
    fun getUpdatedProgressBar(): LiveData<Boolean> {
        return updateArticleProgressBar
    }

}