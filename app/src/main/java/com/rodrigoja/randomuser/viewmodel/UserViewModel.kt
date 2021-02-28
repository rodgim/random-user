package com.rodrigoja.randomuser.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rodrigoja.randomuser.data.database.toUserList
import com.rodrigoja.randomuser.di.DaggerAppComponent
import com.rodrigoja.randomuser.model.User
import com.rodrigoja.randomuser.repository.UserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UserViewModel: ViewModel() {
    @Inject
    lateinit var repository: UserRepository

    private var page: Int = 1

    private val compositeDisposable by lazy { CompositeDisposable() }

    private val _users by lazy { MutableLiveData<List<User>>() }
    val users: LiveData<List<User>>
        get() = _users

    private val _isInProgress by lazy { MutableLiveData<Boolean>() }
    val isInProgress: LiveData<Boolean>
        get() = _isInProgress

    private val _isError by lazy { MutableLiveData<Boolean>() }
    val isError: LiveData<Boolean>
        get() = _isError

    private val _idUserSaved by lazy { MutableLiveData<Long>() }
    val idUserSaved: LiveData<Long>
        get() = _idUserSaved

    private val _favorites by lazy { MutableLiveData<List<User>>() }
    val favorites: LiveData<List<User>>
        get() = _favorites

    init {
        DaggerAppComponent.create().inject(this)
        onGetUsers()
        getFavorites()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun onGetUsers(){
        compositeDisposable.add(
            repository.getUsers(page, 50)
                .debounce(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    usersList ->
                    _isInProgress.postValue(true)
                    if (usersList?.results != null && usersList.results.isNotEmpty()){
                        _isError.postValue(false)
                        _users.postValue(usersList.results)
                    }
                    _isInProgress.postValue(false)
                }, {
                    throwable ->
                    _isInProgress.postValue(true)
                    _isError.postValue(true)
                    _isInProgress.postValue(false)

                })
        )
        page++
    }

    private fun getFavorites(){
        compositeDisposable.add(
                repository.getFavorites()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            favoriteList ->
                            if (favoriteList != null && favoriteList.isNotEmpty()){
                                _favorites.postValue(favoriteList.toUserList())
                            }else{
                                _favorites.postValue(emptyList())
                            }
                        }, {
                            throwable ->
                                _favorites.postValue(emptyList())
                        })
        )
    }

    fun insertUser(user: User){
        compositeDisposable.add(
                repository.insertUser(user)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            id ->
                            _idUserSaved.postValue(id)
                        },{
                            _idUserSaved.postValue(0)
                        })
        )
    }
}