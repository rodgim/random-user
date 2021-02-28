package com.rodrigoja.randomuser.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rodrigoja.randomuser.R
import com.rodrigoja.randomuser.databinding.ItemFavoriteBinding
import com.rodrigoja.randomuser.model.User

class FavoriteViewHolder(val itemFavoriteBinding: ItemFavoriteBinding): RecyclerView.ViewHolder(itemFavoriteBinding.root){}

class FavoriteViewHolderAdapter(private val users: ArrayList<User>): RecyclerView.Adapter<FavoriteViewHolder>() {
    var callback: (User) -> Unit = { u -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val itemFavoriteBinding: ItemFavoriteBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_favorite, parent, false
        )
        return FavoriteViewHolder(itemFavoriteBinding)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.itemFavoriteBinding.user = users[position]
        holder.itemView.setOnClickListener {
            callback(users[position])
        }
    }

    fun addData(list: List<User>){
        users.addAll(list)
        notifyDataSetChanged()
    }
}