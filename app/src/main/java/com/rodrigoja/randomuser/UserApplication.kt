package com.rodrigoja.randomuser

import androidx.multidex.MultiDexApplication
import com.rodrigoja.randomuser.data.database.UserDatabase

class UserApplication: MultiDexApplication() {
    companion object{
        lateinit var instance: UserApplication
        lateinit var database: UserDatabase
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        database = UserDatabase.invoke(this)
    }
}