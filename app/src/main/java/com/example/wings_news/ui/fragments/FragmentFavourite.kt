package com.example.wings_news.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wings_news.R
import com.example.wings_news.data.model.Article
import com.example.wings_news.ui.MainActivity
import com.example.wings_news.ui.adapters.NewsAdapter
import com.example.wings_news.ui.viewModel.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_favourite.*
import kotlinx.android.synthetic.main.fragment_home.*

class FragmentFavourite : Fragment(R.layout.fragment_favourite), NewsAdapter.MyClick {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        setRecyclerView()
        initViewModel()
        deleteArticle()

    }

    private fun initViewModel() {
        viewModel.getAllNews().observe(viewLifecycleOwner, Observer {
            newsAdapter.submitList(it)
        })
    }

    private fun deleteArticle() {
        val itemTouch = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(requireView(), R.string.delete_article, Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo") {
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }

        }

        ItemTouchHelper(itemTouch).apply {
            attachToRecyclerView(favourite_RecyclerView)
        }

    }

    private fun setRecyclerView() {
        newsAdapter = NewsAdapter(requireContext(), this)
        favourite_RecyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onArticleClick(article: Article) {
        val action = FragmentFavouriteDirections.actionFragmentFavouriteToFragmentArticle(article)
        findNavController().navigate(action)
    }

}