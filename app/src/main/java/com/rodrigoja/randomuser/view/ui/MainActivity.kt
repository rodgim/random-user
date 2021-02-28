package com.rodrigoja.randomuser.view.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.MatrixCursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rodrigoja.randomuser.R
import com.rodrigoja.randomuser.di.DaggerAppComponent
import com.rodrigoja.randomuser.internal.USER
import com.rodrigoja.randomuser.model.User
import com.rodrigoja.randomuser.utils.EndLessRecyclerViewScrollListener
import com.rodrigoja.randomuser.view.adapter.FavoriteViewHolderAdapter
import com.rodrigoja.randomuser.view.adapter.SuggestionAdapter
import com.rodrigoja.randomuser.view.adapter.UserViewHolderAdapter
import com.rodrigoja.randomuser.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var userAdapter: UserViewHolderAdapter

    @Inject
    lateinit var favoriteAdapter: FavoriteViewHolderAdapter

    private var suggestionAdapter: SuggestionAdapter? = null

    private var scrollListener: EndLessRecyclerViewScrollListener? = null

    private val viewModel: UserViewModel by viewModels()

    private var cacheUsers: ArrayList<User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DaggerAppComponent.create().inject(this)

        setUpUsersRecyclerView()

        setUpFavoritRecyclerView()

        observeLiveData()

        setUpSuggestionView()
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
            goToDetail(it)
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
            goToDetail(it)
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
                    cacheUsers.clear()
                    cacheUsers.addAll(it)
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

    private fun setUpSuggestionView(){
        suggestionAdapter = SuggestionAdapter(this@MainActivity, null, false)
        suggestionAdapter?.callback = {
            for (item in cacheUsers){
                if (item.email == it){
                    goToDetail(item)
                    break
                }
            }
        }
    }

    private fun goToDetail(user: User){
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(USER, user)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInf: MenuInflater = menuInflater
        menuInf.inflate(R.menu.menu_main_activity, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        var searchManager: SearchManager? = applicationContext.getSystemService(Context.SEARCH_SERVICE) as SearchManager?

        searchItem?.let {
            val searchView = it.actionView as SearchView
            searchView?.let {
                sView ->
                sView.setSearchableInfo(searchManager?.getSearchableInfo(this@MainActivity.componentName))
                sView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        newText?.let {
                            createCursor(it)
                        }
                        return false
                    }
                })
                sView.suggestionsAdapter = suggestionAdapter
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun createCursor(s: String){
        val cursor = MatrixCursor(arrayOf(BaseColumns._ID, "name", "email"))
        var tempList: ArrayList<User> = ArrayList()
        for (item in cacheUsers){
            if (item.name.first.contains(s, true)
                    || item.name.last.contains(s, true)){
                tempList.add(item)
            }
        }

        for ((index, item) in tempList.withIndex()){
            val fullName = "${item.name.first} ${item.name.last}"
            val row = arrayOf(
                    index.toString(),
                    fullName,
                    item.email
            )
            cursor.addRow(row)
        }
        suggestionAdapter?.changeCursor(cursor)
    }
}