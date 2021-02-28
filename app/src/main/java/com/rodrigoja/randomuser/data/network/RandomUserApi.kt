package com.rodrigoja.randomuser.data.network

import com.rodrigoja.randomuser.model.UsersResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserApi {
    @GET("api/")
    fun getUsers(
        @Query("page") page: Int,
        @Query("results") quantity: Int
    ): Observable<UsersResult>
}