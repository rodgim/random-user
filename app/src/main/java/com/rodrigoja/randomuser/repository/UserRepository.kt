package com.rodrigoja.randomuser.repository

import com.rodrigoja.randomuser.UserApplication
import com.rodrigoja.randomuser.data.database.UserEntity
import com.rodrigoja.randomuser.data.database.toDataEntity
import com.rodrigoja.randomuser.data.network.RandomUserApi
import com.rodrigoja.randomuser.di.DaggerAppComponent
import com.rodrigoja.randomuser.model.User
import com.rodrigoja.randomuser.model.UsersResult
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class UserRepository {
    @Inject
    lateinit var randomUserApiService: RandomUserApi

    init {
        DaggerAppComponent.create().inject(this)
    }

    fun getUsers(page: Int, quantity: Int): Observable<UsersResult>{
        return randomUserApiService.getUsers(page, quantity)
    }

    fun getFavorites(): Single<List<UserEntity>>{
        return UserApplication.database.userDao().queryUser()
    }

    fun insertUser(user: User): Single<Long>{
        return Single.fromCallable{
            UserApplication.database.userDao().insertUser(user.toDataEntity())
        }
    }
}