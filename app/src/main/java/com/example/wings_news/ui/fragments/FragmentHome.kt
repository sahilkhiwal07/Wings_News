package com.example.wings_news.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wings_news.R
import com.example.wings_news.data.model.Article
import com.example.wings_news.ui.MainActivity
import com.example.wings_news.ui.adapters.NewsAdapter
import com.example.wings_news.ui.viewModel.NewsViewModel
import com.example.wings_news.utils.Constants.Companion.COUNTRY
import com.example.wings_news.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.example.wings_news.utils.Resource
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.items.*

class FragmentHome : Fragment(R.layout.fragment_home), NewsAdapter.MyClick {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    val TAG = "HOME_FRAGMENT"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        setRecyclerView()
        initViewModel()

    }

    private fun initViewModel() {

        viewModel.getUpdatedProgressBar().observe(viewLifecycleOwner, Observer {
            if (it) showArticleProgressBar()
            else hideArticleProgressBar()
        })

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { news ->
                        newsAdapter.submitList(news.articles.toList())
                        val totalPages = news.totalResults / QUERY_PAGE_SIZE * 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage){
                            newsRecyclerView.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error occurred $message")
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun setRecyclerView() {
        newsAdapter = NewsAdapter(requireContext(), this)
        newsRecyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(this@FragmentHome.scrollListener)
        }
    }


    // Page Progress Bar
    private fun hideProgressBar() {
        news_page_loading?.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        news_page_loading?.visibility = View.VISIBLE
        isLoading = true
    }

    // Article Progress Bar
    private fun hideArticleProgressBar() {
        load_bar?.visibility = View.INVISIBLE
    }

    private fun showArticleProgressBar() {
        load_bar?.visibility = View.VISIBLE
    }

    override fun onArticleClick(article: Article) {
        val action = FragmentHomeDirections.actionFragmentHomeToFragmentArticle(article)
        findNavController().navigate(action)
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem
                    && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getBreakingNews()
                isScrolling = false
            }
        }
    }


}