package com.rodrigoja.randomuser.data.database

import androidx.room.*
import io.reactivex.Single

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity): Long

    @Delete
    fun deleteUser(user: UserEntity): Int

    @Query("SELECT * from user")
    fun queryUser(): Single<List<UserEntity>>

    @Query("SELECT * FROM user WHERE idUser == :userId LIMIT 1")
    fun getUser(userId: String): Single<List<UserEntity>>
}