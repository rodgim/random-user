package com.rodrigoja.randomuser.utils

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class EndLessRecyclerViewScrollListener: RecyclerView.OnScrollListener {
    private var visibleThreshold: Int = 5
    private var currentPage: Int = 0
    private var previousTotalItemCount: Int = 0
    private var loading: Boolean = true
    private var startingPageIndex: Int = 0

    private var mLayoutManager: RecyclerView.LayoutManager

    constructor(layoutManager: LinearLayoutManager){
        mLayoutManager = layoutManager
    }

    constructor(layoutManager: GridLayoutManager){
        mLayoutManager = layoutManager
        visibleThreshold *= layoutManager.spanCount
    }

    constructor(layoutManager: StaggeredGridLayoutManager){
        mLayoutManager = layoutManager
        visibleThreshold *= layoutManager.spanCount
    }

    fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int{
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices){
            if (i == 0){
                maxSize = lastVisibleItemPositions[i]
            }else if (lastVisibleItemPositions[i] > maxSize){
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0){
            var lastVisibleItemPosition = 0
            var totalItemCount: Int = mLayoutManager.itemCount

            if (mLayoutManager is StaggeredGridLayoutManager){
                val lastVisibleItemPositions = (mLayoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
            }else if (mLayoutManager is GridLayoutManager){
                lastVisibleItemPosition = (mLayoutManager as GridLayoutManager).findLastCompletelyVisibleItemPosition()
            }else if (mLayoutManager is LinearLayoutManager){
                lastVisibleItemPosition = (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }

            if (totalItemCount < previousTotalItemCount){
                this.currentPage = this.startingPageIndex
                this.previousTotalItemCount = totalItemCount
                if (totalItemCount == 0){
                    this.loading = true
                }
            }

            if (loading && (totalItemCount > previousTotalItemCount)){
                loading = false
                previousTotalItemCount = totalItemCount
            }

            if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount){
                currentPage++
                onLoadMore(currentPage, totalItemCount, recyclerView)
                loading = true
            }
        }
    }

    fun resetState(){
        this.currentPage = this.startingPageIndex
        this.previousTotalItemCount = 0
        this.loading = true
    }

    abstract fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?)
}