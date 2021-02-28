package com.rodrigoja.randomuser.internal

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class GlideModuleApp: AppGlideModule(){}

@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String?){
    url?.let {
        GlideApp.with(imageView.context)
                .load(it)
                .into(imageView)
    }
}

@BindingAdapter("imageUrlCircle")
fun setImageUrlCircle(imageView: ImageView, url: String?){
    url?.let {
        GlideApp.with(imageView.context)
                .load(it)
                .into(imageView)
    }
}