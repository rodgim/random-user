package com.rodrigoja.randomuser.view.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rodrigoja.randomuser.R
import com.rodrigoja.randomuser.di.DaggerAppComponent
import com.rodrigoja.randomuser.internal.USER
import com.rodrigoja.randomuser.utils.EndLessRecyclerViewScrollListener
import com.rodrigoja.randomuser.view.adapter.FavoriteViewHolderAdapter
import com.rodrigoja.randomuser.view.adapter.UserViewHolderAdapter
import com.rodrigoja.randomuser.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var userAdapter: UserViewHolderAdapter

    @Inject
    lateinit var favoriteAdapter: FavoriteViewHolderAdapter

    private var scrollListener: EndLessRecyclerViewScrollListener? = null

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DaggerAppComponent.create().inject(this)

        setUpUsersRecyclerView()

        setUpFavoritRecyclerView()

        observeLiveData()

    }

    private fun setUpUsersRecyclerView(){
        val layoutGridLayoutManager = GridLayoutManager(
                this,
                2,
                GridLayoutManager.VERTICAL,
                false
        )
        rvUsers.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = userAdapter
            layoutManager = layoutGridLayoutManager
        }

        scrollListener = object : EndLessRecyclerViewScrollListener(layoutGridLayoutManager){
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                fetchUsers()
            }
        }

        rvUsers.addOnScrollListener(scrollListener!!)

        userAdapter.callback = {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(USER, it)
            startActivity(intent)
        }

        fetchUsers()
    }

    private fun setUpFavoritRecyclerView(){
        val linearLayoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        )

        rvFavorite.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = favoriteAdapter
            layoutManager = linearLayoutManager
        }

        favoriteAdapter.callback = {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(USER, it)
            startActivity(intent)
        }

        viewModel.getFavorites()
    }

    private fun observeLiveData(){
        observeIsProgress()
        observeIsError()
        observeUserList()
        observeFavoriteList()
    }

    private fun observeIsProgress(){
        viewModel.isInProgress.observe(this, Observer { isLoading ->
            isLoading.let {
                if (it){
                    empty_text.visibility = View.GONE
                    rvUsers.visibility = View.GONE
                    fetch_progress.visibility = View.VISIBLE
                }else{
                    fetch_progress.visibility = View.GONE
                }
            }
        })
    }

    private fun observeIsError(){
        viewModel.isError.observe(this, Observer { isError ->
            isError.let {
                if (it){
                    disableViewsOnError()
                }else{
                    empty_text.visibility = View.GONE
                    fetch_progress.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun disableViewsOnError(){
        fetch_progress.visibility = View.VISIBLE
        empty_text.visibility = View.VISIBLE
        rvUsers.visibility = View.GONE
        userAdapter.addData(emptyList())
        fetch_progress.visibility = View.GONE
    }

    private fun observeUserList(){
        viewModel.users.observe(this, Observer { users ->
            users.let {
                if (it != null && it.isNotEmpty()){
                    userAdapter.addData(it)
                    fetch_progress.visibility = View.VISIBLE
                    rvUsers.visibility = View.VISIBLE
                    empty_text.visibility = View.GONE
                    fetch_progress.visibility = View.GONE
                }else{
                    disableViewsOnError()
                }
            }
        })
    }

    private fun observeFavoriteList(){
        viewModel.favorites.observe(this, Observer {
            users ->
            users?.let {
                favoriteAdapter.addData(it)
                if (it.isNotEmpty()){
                    rvFavorite.visibility = View.VISIBLE
                }else{
                    rvFavorite.visibility = View.GONE
                }
            }
        })
    }

    private fun fetchUsers(){
        viewModel.onGetUsers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavorites()
    }
}