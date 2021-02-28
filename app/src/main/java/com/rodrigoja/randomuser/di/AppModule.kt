package com.rodrigoja.randomuser.di

import com.rodrigoja.randomuser.data.network.RandomUserApi
import com.rodrigoja.randomuser.data.network.RandomUserApiService
import com.rodrigoja.randomuser.model.User
import com.rodrigoja.randomuser.repository.UserRepository
import com.rodrigoja.randomuser.view.adapter.FavoriteViewHolderAdapter
import com.rodrigoja.randomuser.view.adapter.UserViewHolderAdapter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule{
    @Singleton
    @Provides
    fun provideApi(): RandomUserApi = RandomUserApiService.getClient()

    @Provides
    fun provideUserRepository() = UserRepository()

    @Provides
    fun provideListUser() = ArrayList<User>()

    @Provides
    fun provideUserAdapter(data: ArrayList<User>): UserViewHolderAdapter = UserViewHolderAdapter(data)

    @Provides
    fun provideFavoriteAdapter(data: ArrayList<User>): FavoriteViewHolderAdapter = FavoriteViewHolderAdapter(data)
}