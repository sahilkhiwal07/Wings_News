package com.example.wings_news.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wings_news.R
import com.example.wings_news.data.model.Article
import com.example.wings_news.ui.MainActivity
import com.example.wings_news.ui.adapters.NewsAdapter
import com.example.wings_news.ui.viewModel.NewsViewModel
import com.example.wings_news.utils.Constants.Companion.SEARCH_TIME_DELAY
import com.example.wings_news.utils.Resource
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.items.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentSearch : Fragment(R.layout.fragment_search), NewsAdapter.MyClick {

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var viewModel: NewsViewModel
    var job: Job? = null

    val TAG = "SEARCH_FRAGMENT"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        setRecyclerView()
        initViewModel()
        searchRequest()

    }

    private fun searchRequest() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                job?.cancel()
                job = MainScope().launch {
                    delay(SEARCH_TIME_DELAY)
                    viewModel.getSearchNews(newText.toString())
                }
                return true
            }

        })
    }

    private fun initViewModel() {

        viewModel.getUpdatedProgressBar().observe(viewLifecycleOwner, Observer {
            if (it) showArticleProgressBar()
            else hideArticleProgressBar()
        })

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { news ->
                        newsAdapter.submitList(news.articles)
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
        search_recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    // On Article Click
    override fun onArticleClick(article: Article) {
        val action = FragmentSearchDirections.actionFragmentSearchToFragmentArticle(article)
        findNavController().navigate(action)
    }

    // Page Progress Bar
    private fun showProgressBar() {
        search_Loading?.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        search_Loading?.visibility = View.INVISIBLE
    }

    // Article Progress Bar
    private fun hideArticleProgressBar() {
        load_bar?.visibility = View.INVISIBLE
    }

    private fun showArticleProgressBar() {
        load_bar?.visibility = View.VISIBLE
    }

}