package com.example.wings_news.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.wings_news.R
import com.example.wings_news.ui.MainActivity
import com.example.wings_news.ui.viewModel.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class FragmentArticle : Fragment(R.layout.fragment_article) {

    private lateinit var viewModel: NewsViewModel
    private val args: FragmentArticleArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        showNewsSite()
        onSavedButtonClicked()

    }

    private fun showNewsSite() {
        val article = args.article
        loading_web?.visibility = View.VISIBLE
        webView.apply {
            settings.domStorageEnabled = true
            settings.javaScriptEnabled = true
            settings.loadsImagesAutomatically = true
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }

        if (webView.isShown) {
            loading_web?.visibility = View.INVISIBLE
        }
    }

    private fun onSavedButtonClicked() {
        val article = args.article
        favourite.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(requireView(), R.string.saved_article, Snackbar.LENGTH_SHORT).show()
        }
    }

}