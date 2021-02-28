package com.rodrigoja.randomuser.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rodrigoja.randomuser.R
import com.rodrigoja.randomuser.databinding.ItemUserBinding
import com.rodrigoja.randomuser.model.User

class UserViewHolder(val itemUserBinding: ItemUserBinding): RecyclerView.ViewHolder(itemUserBinding.root){}

class UserViewHolderAdapter(private val users: ArrayList<User>): RecyclerView.Adapter<UserViewHolder>() {
    var callback: (User) -> Unit = { u -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemUserBinding: ItemUserBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_user, parent, false
        )
        return UserViewHolder(itemUserBinding)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.itemUserBinding.user = users[position]
        holder.itemView.setOnClickListener {
            callback(users[position])
        }
    }

    fun addData(list: List<User>){
        users.addAll(list)
        notifyDataSetChanged()
    }
}